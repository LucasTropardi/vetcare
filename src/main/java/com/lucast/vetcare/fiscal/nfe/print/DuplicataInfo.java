package com.lucast.vetcare.fiscal.nfe.print;

import java.math.BigDecimal;

public class DuplicataInfo {
    // Coluna 1
    private String nDup1;
    private String dVenc1;
    private BigDecimal vDup1;

    // Coluna 2
    private String nDup2;
    private String dVenc2;
    private BigDecimal vDup2;

    // Coluna 3
    private String nDup3;
    private String dVenc3;
    private BigDecimal vDup3;

    public DuplicataInfo(Builder builder) {
        this.nDup1 = builder.nDup1;
        this.dVenc1 = builder.dVenc1;
        this.vDup1 = builder.vDup1;
        this.nDup2 = builder.nDup2;
        this.dVenc2 = builder.dVenc2;
        this.vDup2 = builder.vDup2;
        this.nDup3 = builder.nDup3;
        this.dVenc3 = builder.dVenc3;
        this.vDup3 = builder.vDup3;
    }

    public String getnDup1() {
        return nDup1;
    }

    public void setnDup1(String nDup1) {
        this.nDup1 = nDup1;
    }

    public String getdVenc1() {
        return dVenc1;
    }

    public void setdVenc1(String dVenc1) {
        this.dVenc1 = dVenc1;
    }

    public BigDecimal getvDup1() {
        return vDup1;
    }

    public void setvDup1(BigDecimal vDup1) {
        this.vDup1 = vDup1;
    }

    public String getnDup2() {
        return nDup2;
    }

    public void setnDup2(String nDup2) {
        this.nDup2 = nDup2;
    }

    public String getdVenc2() {
        return dVenc2;
    }

    public void setdVenc2(String dVenc2) {
        this.dVenc2 = dVenc2;
    }

    public BigDecimal getvDup2() {
        return vDup2;
    }

    public void setvDup2(BigDecimal vDup2) {
        this.vDup2 = vDup2;
    }

    public String getnDup3() {
        return nDup3;
    }

    public void setnDup3(String nDup3) {
        this.nDup3 = nDup3;
    }

    public String getdVenc3() {
        return dVenc3;
    }

    public void setdVenc3(String dVenc3) {
        this.dVenc3 = dVenc3;
    }

    public BigDecimal getvDup3() {
        return vDup3;
    }

    public void setvDup3(BigDecimal vDup3) {
        this.vDup3 = vDup3;
    }

    public static class Builder {
        private String nDup1;
        private String dVenc1;
        private BigDecimal vDup1;
        private String nDup2;
        private String dVenc2;
        private BigDecimal vDup2;
        private String nDup3;
        private String dVenc3;
        private BigDecimal vDup3;

        public Builder nDup1(String nDup1) {
            this.nDup1 = nDup1;
            return this;
        }

        public Builder dVenc1(String dVenc1) {
            this.dVenc1 = dVenc1;
            return this;
        }

        public Builder vDup1(BigDecimal vDup1) {
            this.vDup1 = vDup1;
            return this;
        }

        public Builder nDup2(String nDup2) {
            this.nDup2 = nDup2;
            return this;
        }

        public Builder dVenc2(String dVenc2) {
            this.dVenc2 = dVenc2;
            return this;
        }

        public Builder vDup2(BigDecimal vDup2) {
            this.vDup2 = vDup2;
            return this;
        }

        public Builder nDup3(String nDup3) {
            this.nDup3 = nDup3;
            return this;
        }

        public Builder dVenc3(String dVenc3) {
            this.dVenc3 = dVenc3;
            return this;
        }

        public Builder vDup3(BigDecimal vDup3) {
            this.vDup3 = vDup3;
            return this;
        }

        public DuplicataInfo build() {
            return new DuplicataInfo(this);
        }
    }
}
