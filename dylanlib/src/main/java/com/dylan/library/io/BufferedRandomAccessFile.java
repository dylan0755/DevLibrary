package com.dylan.library.io;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;


public final class BufferedRandomAccessFile extends RandomAccessFile
{
    static final int LogBuffSz_ = 16; // 64K buffer
    public static final int BuffSz_ = (1 << LogBuffSz_);
    static final long BuffMask_ = ~(((long) BuffSz_) - 1L);

    private String path_;


    private boolean dirty_; // true iff unflushed bytes exist
    private boolean syncNeeded_; // dirty_ can be cleared by e.g. seek, so track sync separately
    private long curr_; // current position in file
    private long lo_, hi_; // bounds on characters in "buff"
    private byte[] buff_; // local buffer
    private long maxHi_; // this.lo + this.buff.length
    private boolean hitEOF_; // buffer contains last file block?
    private long diskPos_; // disk position
  

    public BufferedRandomAccessFile(File file, String mode) throws IOException
    {
        this(file, mode, 0);
    }

    public BufferedRandomAccessFile(File file, String mode, int size) throws IOException
    {
        super(file, mode);
        path_ = file.getAbsolutePath();
        this.init(size);
    }

    /**
     * Open a new <code>BufferedRandomAccessFile</code> on the file named
     * <code>name</code> in mode <code>mode</code>, which should be "r" for
     * reading only, or "rw" for reading and writing.
     */
    public BufferedRandomAccessFile(String name, String mode) throws IOException
    {
        this(name, mode, 0);
    }

    public BufferedRandomAccessFile(String name, String mode, int size) throws FileNotFoundException
    {
        super(name, mode);
        path_ = name;
        this.init(size);
    }

    private void init(int size)
    {
        this.dirty_ = false;
        this.lo_ = this.curr_ = this.hi_ = 0;
        this.buff_ = (size > BuffSz_) ? new byte[size] : new byte[BuffSz_];
        this.maxHi_ = (long) BuffSz_;
        this.hitEOF_ = false;
        this.diskPos_ = 0L;
    }

    public String getPath()
    {
        return path_;
    }

    public void sync() throws IOException
    {
        if (syncNeeded_)
        {
            flush();
            getChannel().force(true);
            syncNeeded_ = false;
        }
    }


    public void close() throws IOException
    {
        this.flush();
        this.buff_ = null;
        super.close();
    }


    public void flush() throws IOException
    {
        this.flushBuffer();
    }

    /* Flush any dirty bytes in the buffer to disk. */
    private void flushBuffer() throws IOException
    {
        if (this.dirty_)
        {
            if (this.diskPos_ != this.lo_)
                super.seek(this.lo_);
            int len = (int) (this.curr_ - this.lo_);
            super.write(this.buff_, 0, len);
            this.diskPos_ = this.curr_;
            this.dirty_ = false;
        }
    }

    /*
     * Read at most "this.buff.length" bytes into "this.buff", returning the
     * number of bytes read. If the return result is less than
     * "this.buff.length", then EOF was read.
     */
    private int fillBuffer() throws IOException
    {
        int cnt = 0;
        int rem = this.buff_.length;
        while (rem > 0)
        {
            int n = super.read(this.buff_, cnt, rem);
            if (n < 0)
                break;
            cnt += n;
            rem -= n;
        }
        if ( (cnt < 0) && (this.hitEOF_ = (cnt < this.buff_.length)) )
        {
            // make sure buffer that wasn't read is initialized with -1
            Arrays.fill(this.buff_, cnt, this.buff_.length, (byte) 0xff);
        }
        this.diskPos_ += cnt;
        return cnt;
    }

    /*
     * This method positions <code>this.curr</code> at position <code>pos</code>.
     * If <code>pos</code> does not fall in the current buffer, it flushes the
     * current buffer and loads the correct one.<p>
     *
     * On exit from this routine <code>this.curr == this.hi</code> iff <code>pos</code>
     * is at or past the end-of-file, which can only happen if the file was
     * opened in read-only mode.
     */
    public void seek(long pos) throws IOException
    {
        if (pos >= this.hi_ || pos < this.lo_)
        {
            // seeking outside of current buffer -- flush and read
            this.flushBuffer();
            this.lo_ = pos & BuffMask_; // start at BuffSz boundary
            this.maxHi_ = this.lo_ + (long) this.buff_.length;
            if (this.diskPos_ != this.lo_)
            {
                super.seek(this.lo_);
                this.diskPos_ = this.lo_;
            }
            int n = this.fillBuffer();
            this.hi_ = this.lo_ + (long) n;
        }
        else
        {
            // seeking inside current buffer -- no read required
            if (pos < this.curr_)
            {
                // if seeking backwards, we must flush to maintain V4
                this.flushBuffer();
            }
        }
        this.curr_ = pos;
    }

    public long getFilePointer()
    {
        return this.curr_;
    }

    public long length() throws IOException
    {
        // max accounts for the case where we have written past the old file length, but not yet flushed our buffer
        return Math.max(this.curr_, super.length());
    }

    public int read() throws IOException
    {
        if (this.curr_ >= this.hi_)
        {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (this.hitEOF_)
                return -1;

            // slow path -- read another buffer
            this.seek(this.curr_);
            if (this.curr_ == this.hi_)
                return -1;
        }
        byte res = this.buff_[(int) (this.curr_ - this.lo_)];
        this.curr_++;
        return ((int) res) & 0xFF; // convert byte -> int
    }

    public int read(byte[] b) throws IOException
    {
        return this.read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException
    {
        if (this.curr_ >= this.hi_)
        {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (this.hitEOF_)
                return -1;

            // slow path -- read another buffer
            this.seek(this.curr_);
            if (this.curr_ == this.hi_)
                return -1;
        }
        len = Math.min(len, (int) (this.hi_ - this.curr_));
        int buffOff = (int) (this.curr_ - this.lo_);
        System.arraycopy(this.buff_, buffOff, b, off, len);
        this.curr_ += len;
        return len;
    }

    public void write(int b) throws IOException
    {
        if (this.curr_ >= this.hi_)
        {
            if (this.hitEOF_ && this.hi_ < this.maxHi_)
            {
                // at EOF -- bump "hi"
                this.hi_++;
            }
            else
            {
                // slow path -- write current buffer; read next one
                this.seek(this.curr_);
                if (this.curr_ == this.hi_)
                {
                    // appending to EOF -- bump "hi"
                    this.hi_++;
                }
            }
        }
        this.buff_[(int) (this.curr_ - this.lo_)] = (byte) b;
        this.curr_++;
        this.dirty_ = true;
        syncNeeded_ = true;
    }

    public void write(byte[] b) throws IOException
    {
        this.write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException
    {
        while (len > 0)
        {
            int n = this.writeAtMost(b, off, len);
            off += n;
            len -= n;
            this.dirty_ = true;
            syncNeeded_ = true;
        }
    }

    /*
     * Write at most "len" bytes to "b" starting at position "off", and return
     * the number of bytes written.
     */
    private int writeAtMost(byte[] b, int off, int len) throws IOException
    {
        if (this.curr_ >= this.hi_)
        {
            if (this.hitEOF_ && this.hi_ < this.maxHi_)
            {
                // at EOF -- bump "hi"
                this.hi_ = this.maxHi_;
            }
            else
            {
                // slow path -- write current buffer; read next one
                this.seek(this.curr_);
                if (this.curr_ == this.hi_)
                {
                    // appending to EOF -- bump "hi"
                    this.hi_ = this.maxHi_;
                }
            }
        }
        len = Math.min(len, (int) (this.hi_ - this.curr_));
        int buffOff = (int) (this.curr_ - this.lo_);
        System.arraycopy(b, off, this.buff_, buffOff, len);
        this.curr_ += len;
        return len;
    }
}