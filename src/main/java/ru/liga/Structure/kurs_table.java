package ru.liga.Structure;

public class kurs_table {
    public static class Kurs {
        private final String nominal;
        private final String data;
        private final Double curs;
        private final String cdx;

        public Kurs(String[] row) {
            this.nominal = row[0];
            this.data = row[1];
            this.curs = Double.parseDouble(row[2].replace(",", "."));
            this.cdx = row[3];
        }

        public String getNominal() {
            return nominal;
        }

        public String getData() {
            return data;
        }

        public Double getCurs() {
            return curs;
        }

        public String getCdx() {
            return cdx;
        }

    }
}
