package com.dylan.library.graphics;

import android.graphics.Color;

/**
 * Author: Dylan
 * Date: 2022/03/25
 * Desc:
 */
public class ColorUtils {


    /**
     * @param color
     * @param fraction
     * @return
     */
    public static int  changeAlpha(int color,float fraction){
        if (fraction<0||fraction>1)return color;
        int red= Color.red(color);
        int green= Color.green(color);
        int blue=Color.blue(color);
        int alpha= (int) (Color.alpha(color)*fraction);
        return Color.argb(alpha,red,green,blue);
    }

    /**
     * @param hexColor
     * @param fraction
     * @return
     */
    public static int  changeAlpha(String hexColor,float fraction){
        int color=Color.parseColor(hexColor);
        return changeAlpha(color,fraction);
    }



    public static String HEX_ALPHA_100="FF";
    public static String HEX_ALPHA_99="FC";
    public static String HEX_ALPHA_98="FA";
    public static String HEX_ALPHA_97="F7";
    public static String HEX_ALPHA_96="F5";
    public static String HEX_ALPHA_95="F2";
    public static String HEX_ALPHA_94="F0";
    public static String HEX_ALPHA_93="ED";
    public static String HEX_ALPHA_92="EB";
    public static String HEX_ALPHA_91="E8";
    public static String HEX_ALPHA_90="E6";
    public static String HEX_ALPHA_89="E3";
    public static String HEX_ALPHA_88="E0";
    public static String HEX_ALPHA_87="DE";
    public static String HEX_ALPHA_86="DB";
    public static String HEX_ALPHA_85="D9";
    public static String HEX_ALPHA_84="D6";
    public static String HEX_ALPHA_83="D4";
    public static String HEX_ALPHA_82="D1";
    public static String HEX_ALPHA_81="CF";
    public static String HEX_ALPHA_80="CC";
    public static String HEX_ALPHA_79="C9";
    public static String HEX_ALPHA_78="C7";
    public static String HEX_ALPHA_77="C4";
    public static String HEX_ALPHA_76="C2";
    public static String HEX_ALPHA_75="BF";
    public static String HEX_ALPHA_74="BD";
    public static String HEX_ALPHA_73="BA";
    public static String HEX_ALPHA_72="B8";
    public static String HEX_ALPHA_71="B5";
    public static String HEX_ALPHA_70="B3";
    public static String HEX_ALPHA_69="B0";
    public static String HEX_ALPHA_68="AD";
    public static String HEX_ALPHA_67="AB";
    public static String HEX_ALPHA_66="A8";
    public static String HEX_ALPHA_65="A6";
    public static String HEX_ALPHA_64="A3";
    public static String HEX_ALPHA_63="A1";
    public static String HEX_ALPHA_62="9E";
    public static String HEX_ALPHA_61="9C";
    public static String HEX_ALPHA_60="99";
    public static String HEX_ALPHA_59="96";
    public static String HEX_ALPHA_58="94";
    public static String HEX_ALPHA_57="91";
    public static String HEX_ALPHA_56="8F";
    public static String HEX_ALPHA_55="8C";
    public static String HEX_ALPHA_54="8A";
    public static String HEX_ALPHA_53="87";
    public static String HEX_ALPHA_52="85";
    public static String HEX_ALPHA_51="82";
    public static String HEX_ALPHA_50="80";
    public static String HEX_ALPHA_49="7D";
    public static String HEX_ALPHA_48="7A";
    public static String HEX_ALPHA_47="78";
    public static String HEX_ALPHA_46="75";
    public static String HEX_ALPHA_45="73";
    public static String HEX_ALPHA_44="70";
    public static String HEX_ALPHA_43="6E";
    public static String HEX_ALPHA_42="6B";
    public static String HEX_ALPHA_41="69";
    public static String HEX_ALPHA_40="66";
    public static String HEX_ALPHA_39="63";
    public static String HEX_ALPHA_38="61";
    public static String HEX_ALPHA_37="5E";
    public static String HEX_ALPHA_36="5C";
    public static String HEX_ALPHA_35="59";
    public static String HEX_ALPHA_34="57";
    public static String HEX_ALPHA_33="54";
    public static String HEX_ALPHA_32="52";
    public static String HEX_ALPHA_31="4F";
    public static String HEX_ALPHA_30="4D";
    public static String HEX_ALPHA_29="4A";
    public static String HEX_ALPHA_28="47";
    public static String HEX_ALPHA_27="45";
    public static String HEX_ALPHA_26="42";
    public static String HEX_ALPHA_25="40";
    public static String HEX_ALPHA_24="3D";
    public static String HEX_ALPHA_23="3B";
    public static String HEX_ALPHA_22="38";
    public static String HEX_ALPHA_21="36";
    public static String HEX_ALPHA_20="33";
    public static String HEX_ALPHA_19="30";
    public static String HEX_ALPHA_18="2E";
    public static String HEX_ALPHA_17="2B";
    public static String HEX_ALPHA_16="29";
    public static String HEX_ALPHA_15="26";
    public static String HEX_ALPHA_14="24";
    public static String HEX_ALPHA_13="21";
    public static String HEX_ALPHA_12="1F";
    public static String HEX_ALPHA_11="1C";
    public static String HEX_ALPHA_10="1A";
    public static String HEX_ALPHA_9="17";
    public static String HEX_ALPHA_8="14";
    public static String HEX_ALPHA_7="12";
    public static String HEX_ALPHA_6="0F";
    public static String HEX_ALPHA_5="0D";
    public static String HEX_ALPHA_4="0A";
    public static String HEX_ALPHA_3="08";
    public static String HEX_ALPHA_2="05";
    public static String HEX_ALPHA_1="03";
    public static String HEX_ALPHA_0="00";

}
