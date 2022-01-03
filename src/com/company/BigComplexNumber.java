package com.company;

import java.math.BigDecimal;
import java.math.MathContext;

public class BigComplexNumber extends ComplexNumber{
    private BigDecimal realBig = null;
    private BigDecimal ImgBig = null;

    public BigComplexNumber(BigDecimal realBig, BigDecimal ImgBig){
        this.realBig = realBig;
        this.ImgBig = ImgBig;
    }

    public static BigComplexNumber add(BigComplexNumber z1, BigComplexNumber z2) {
        BigDecimal _realBig = z1.getRealBig().add(z2.getRealBig());
        BigDecimal _imgBig = z1.getImgBig().add(z2.getImgBig());
        return new BigComplexNumber(_realBig, _imgBig);
    }

    public BigDecimal getRealBig() {
        return realBig;
    }

    public BigDecimal getImgBig() {
        return ImgBig;
    }

    //i want operator overloading in java ?? java 20 when??
    public static BigComplexNumber pow(BigComplexNumber z, int power, MathContext mc)
    {
        BigComplexNumber output = new BigComplexNumber(z.getRealBig(),z.getImgBig());
        for(int i = 1; i < power; i++)
        {
            BigDecimal _real = output.getRealBig().multiply(z.getRealBig(), mc).subtract(output.getImgBig().multiply(z.getImgBig(), mc));
            BigDecimal _imaginary = output.getRealBig().multiply(z.getImgBig(), mc).add(output.getImgBig().multiply(z.getRealBig(), mc));
            output = new BigComplexNumber(_real,_imaginary);
        }
        return output;
    }
}
