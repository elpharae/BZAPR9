package pkg;

class Banka {
    //neverejne atributy
    private String nazev;
    private Ucet[] ucty;
    private int aktualniPocetUctu;
    private int maximalniPocetUctu;

    // konstruktor - (String nazev, int maximalniPocetUctu) - inicializujte atribut Ucet [] ucty na velikost maximalniPocetUctu
    public Banka(String nazev, int maximalniPocetUctu) {
        this.nazev = nazev;
        this.ucty = new Ucet[maximalniPocetUctu];
        this.aktualniPocetUctu = this.ucty.length;
        this.maximalniPocetUctu = this.ucty.length;

    }
    // alternativa - (String nazev, Ucet [] ucty) - !jen pokud se vam nepodari zakladani uctu! - nastav aktualniPocetUctu a maximalniPocetUctu na velikost pole ucty
    public Banka(String nazev, Ucet[] ucty) {
        this.nazev = nazev;
        this.ucty = ucty;
        this.maximalniPocetUctu = this.ucty.length;
        this.aktualniPocetUctu = this.ucty.length;
    }

    //verejne metody
    void zalozUcet(String kod, double prvniVklad, boolean jeKontokorent) throws ViceUctuNelzeException, KodJizExistujeException {
        if (this.aktualniPocetUctu + 1 > this.maximalniPocetUctu) throw new ViceUctuNelzeException("Nelze založit další účet - dosažen limit");
        else {
            try {
                if (najdiUcet(kod).dejKod() == kod) throw new KodJizExistujeException("Účet s tímto kódem již existuje");
            } catch (UcetNeexistujeException e) {
                System.out.println(e.getMessage());
            }

            Ucet[] pole = new Ucet[this.ucty.length + 1];
            for (int i = 0; i < pole.length; i++) pole[i] = this.ucty[i];
            pole[this.aktualniPocetUctu++] = new Ucet(kod, prvniVklad, jeKontokorent);
            System.out.println("Byl založen účet s kódem: " + kod);
        }
    }

    //Nevraci null, pokud neni nalezen ucet se zadanym kodem, vyhazuje chybu
    public Ucet najdiUcet(String kod) throws UcetNeexistujeException {
        for (Ucet ucet : this.ucty) if (ucet.dejKod() == kod) return ucet;
        throw new UcetNeexistujeException("Účet s tímto kódem neexistuje");
    }

    public void vypisUcet(String kod) {
        try {
            System.out.println(najdiUcet(kod));
        } catch (UcetNeexistujeException e) {
            System.out.println(e.getMessage());
        }
    }

    public void vlozPenize(String kod, double castka) {
        try {
            Ucet ucet = najdiUcet(kod);
            ucet.vlozPenize(castka);
            System.out.println("Na účet s kódem " + kod + " bylo vloženo: " + castka + " CZK");
        } catch (UcetNeexistujeException e) {
            System.out.println(e.getMessage());
        }
    }

    public void vyberPenize(String kod, double castka) {
        try {
            Ucet ucet = najdiUcet(kod);
            ucet.vyberPenize(castka);
            System.out.println("Z účtu s kódem " + kod + " bylo vybráno: " + castka + " CZK");
        } catch (UcetNeexistujeException | UcetNeniKontokorentException e) {
            System.out.println(e.getMessage());
        }
    }

    public double prumernyZustatek() {
        return celkemNaUctech() / this.ucty.length;
    }


    public double celkemNaUctech() {
        double suma = 0;
        for (Ucet ucet : this.ucty) suma += ucet.dejZustatek();
        return suma;
    }

    public Ucet najdiNejbohatsiUcet() throws BankaNemaUctyException {
        if (this.ucty.length == 0) throw new BankaNemaUctyException("Banka nemá žádné účty");
        else {
            Ucet max = this.ucty[0];
            for (Ucet ucet : this.ucty)
                if (ucet.dejZustatek() > max.dejZustatek()) max = ucet;

            return max;
        }
    }

    public Ucet najdiNejchudsiUcet() throws BankaNemaUctyException {
        if (this.ucty.length == 0) throw new BankaNemaUctyException("Banka nemá žádné účty");
        else {
            Ucet min = this.ucty[0];
            for (Ucet ucet : this.ucty)
                if (ucet.dejZustatek() < min.dejZustatek()) min = ucet;

            return min;
        }
    }

    @Override
    public String toString() {
        return "Nazev banky: " + this.nazev;
    }
}

class Ucet {
    //neverejne atributy
    private String kod;
    private double zustatek;
    private boolean jeKontokorent;

    //konstruktor - (kod, zustatek, jeKontokorent)
    public Ucet(String kod, double zustatek, boolean jeKontokorent) {
        this.kod = kod;
        this.zustatek = zustatek;
        this.jeKontokorent = jeKontokorent;
    }

    //verejne metody
    public String dejKod() {return this.kod;}
    public double dejZustatek() {return this.zustatek;}
    public boolean jeKontokorent() {return this.jeKontokorent;}

    public void vlozPenize(double castka) {
        this.zustatek += castka;
    }

    public void vyberPenize(double castka) throws UcetNeniKontokorentException {
        if ((this.zustatek - castka < 0) && !jeKontokorent) throw new UcetNeniKontokorentException("Tento účet nemůže do mínusu");
        else this.zustatek -= castka;
    }

    @Override
    public String toString() {
        return this.kod + ": " + this.zustatek + " CZK," + (jeKontokorent ? " Kontokorent" : " Není kontokorent");
    }
}

class UcetNeniKontokorentException extends Exception {
    public UcetNeniKontokorentException(String zprava) {super(zprava);}
}

class ViceUctuNelzeException extends Exception {
    public ViceUctuNelzeException(String zprava) {super(zprava);}
}

class KodJizExistujeException extends Exception {
    public KodJizExistujeException(String zprava) {super(zprava);}
}

class UcetNeexistujeException extends Exception {
    public UcetNeexistujeException(String zprava) {super(zprava);}
}

class BankaNemaUctyException extends Exception {
    public BankaNemaUctyException(String zprava) {super(zprava);}
}

public class Main {

    public static void main(String[] args) {
        Ucet[] ucty = new Ucet[]{
            new Ucet("001", 26.6, true),
            new Ucet("002", 3006.2, false),
            new Ucet("003", 8888.8, true)
        };
        Banka banka = new Banka("Banka z Harryho Pottera", ucty);
        System.out.println(banka);
        try {
            System.out.println("Nejbohatší: " + banka.najdiNejbohatsiUcet());
            System.out.println("Nejchudší: " + banka.najdiNejchudsiUcet());
            System.out.println("Průměrný zůstatek: " + banka.prumernyZustatek());
            System.out.println("Celkem na účtech: " + banka.celkemNaUctech());
            banka.vlozPenize("001", 20);
            banka.vyberPenize("001", 500);
            banka.vyberPenize("002", 4000);
            banka.vypisUcet("004");
            banka.zalozUcet("004", 500, true);
            banka.vypisUcet("004");
        } catch (BankaNemaUctyException | ViceUctuNelzeException | KodJizExistujeException e) {
            System.out.println(e.getMessage());
        }
    }
}
