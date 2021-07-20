/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dylan.library.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class RandomUtils {

    private static final Random RANDOM = new Random();


    public RandomUtils() {
    }


    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }


    public static int nextInclusiveExclusiveIntValue(final int startInclusive, final int endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + RANDOM.nextInt(endExclusive - startInclusive);
    }

    public static int nextInclusiveInclusiveIntValue(final int startInclusive, final int endInclusive) {
        if (startInclusive == endInclusive) {
            return startInclusive;
        }
        return startInclusive + RANDOM.nextInt(endInclusive - startInclusive + 1);
    }


    public static double nextInclusiveExclusiveDouble(final double startInclusive, final double endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + ((endExclusive - startInclusive) * RANDOM.nextDouble());
    }

    public static double nextInclusiveInclusiveDouble(final double startInclusive, final double endInclusive, int keepPoint) {
        if (startInclusive == endInclusive) {
            return startInclusive;
        }
        DecimalFormat df = null;
        double inserValue = 0;
        if (keepPoint == 1) {
            inserValue = 0.1;
            df = new DecimalFormat("#.0");
        } else if (keepPoint == 2) {
            inserValue = 0.01;
            df = new DecimalFormat("#.00");
        } else if (keepPoint == 3) {
            inserValue = 0.001;
            df = new DecimalFormat("#.000");
        } else if (keepPoint == 4) {
            inserValue = 0.0001;
            df = new DecimalFormat("#.0000");
        } else if (keepPoint == 5) {
            inserValue = 0.00001;
            df = new DecimalFormat("#.00000");
        } else if (keepPoint == 6) {
            inserValue = 0.000001;
            df = new DecimalFormat("#.000000");
        }
        double value = startInclusive + ((endInclusive - startInclusive + inserValue) * RANDOM.nextDouble());
        if (df != null) {
            df.setRoundingMode(RoundingMode.FLOOR);//不进行四舍五入
            value = Double.parseDouble(df.format(value));
        }
        return value;
    }


    public static float nextInclusiveExclusiveFloat(final float startInclusive, final float endExclusive) {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return startInclusive + ((endExclusive - startInclusive) * RANDOM.nextFloat());
    }

    public static float nextInclusiveInclusiveFloat(final float startInclusive, final float endInclusive, int keepPoint) {
        if (startInclusive == endInclusive) {
            return startInclusive;
        }
        DecimalFormat df = null;
        float inserValue = 0;
        if (keepPoint == 1) {
            inserValue = 0.1f;
            df = new DecimalFormat("#.0");
        } else if (keepPoint == 2) {
            inserValue = 0.01f;
            df = new DecimalFormat("#.00");
        } else if (keepPoint == 3) {
            inserValue = 0.001f;
            df = new DecimalFormat("#.000");
        } else if (keepPoint == 4) {
            inserValue = 0.0001f;
            df = new DecimalFormat("#.0000");
        } else if (keepPoint == 5) {
            inserValue = 0.00001f;
            df = new DecimalFormat("#.00000");
        } else if (keepPoint == 6) {
            inserValue = 0.000001f;
            df = new DecimalFormat("#.000000");
        }

        float value = startInclusive + ((endInclusive - startInclusive + inserValue) * RANDOM.nextFloat());
        if (df != null) {
            df.setRoundingMode(RoundingMode.FLOOR);//不进行四舍五入
            value = Float.parseFloat(df.format(value));
        }
        return value;
    }

    /**
     * @param min
     * @param max
     * @param keepPoint    //保留小数点
     * @param excludeValue
     * @return
     */
    public static float getFloatFromInclusiveInclusiveRange(float min, float max, int keepPoint, float excludeValue) {
        while (true) {
            float num = RandomUtils.nextInclusiveInclusiveFloat(min, max, keepPoint);
            if (num == excludeValue) {
                continue;
            } else {
                return num;
            }
        }
    }

    public static double getDoubleFromInclusiveInclusiveRange(double min, double max, int keepPoint, double excludeValue) {
        while (true) {
            double num = RandomUtils.nextInclusiveInclusiveDouble(min, max, keepPoint);
            if (num == excludeValue) {
                continue;
            } else {
                return num;
            }
        }
    }

    /**
     * 从数组中随即抽出若干项非重复的元素
     */
    public static List<Integer> getRandomDistinctElementsFromSpecArrays(int[] srcArrays, int elementCount) {
        if (elementCount <= 0) elementCount = 1;
        if (elementCount>srcArrays.length)elementCount=srcArrays.length;

        HashMap<Integer,Integer> map = new HashMap<>();
        Random random = new Random();
        while (true) {
            int index = random.nextInt(srcArrays.length);
            if (map.containsKey(index)) {
                continue;
            } else {
                map.put(index,srcArrays[index]);
            }
            if (map.size() == elementCount) break;
        }
        return new ArrayList<>(map.values());
    }




}
