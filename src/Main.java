
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.concurrent.TimeUnit;


public class Main {
    private static int hp = 0;
    private static int maxhp = 0;
    private static double atk = 0;
    private static double def = 0;
    private static double spa = 0; //This is special attack.
    // The difference between atk and spa is that spa is distance mental attacks, while atk are more physical attacks.
    private static double spd = 0; //Similarly, SPD defends from special attacks, while DEF defends from physical attacks
    private static double spe = 0; //This is the speed stat, fastest goes first!
    private static boolean DamageOverTime = false; //This status will inflict damage over time
    private static boolean Imobility = false; //This status has a chance of inhibiting a pokemon from moving
    private static boolean flinch = false; //The flinch status effect. If your opponent outspeeds you and flinches,
    // your move will be invalidated, and your turn is skipped.

    private static int ohp = 0;
    private static int maxohp = 0;
    private static double oatk = 0;
    private static double odef = 0;
    private static double ospa = 0;
    private static double ospd = 0;
    private static double ospe = 0;
    private static boolean ODamageOverTime = false;
    private static boolean OImobility = false;
    private static boolean Oflinch = false;

    private static int x = 0; //This is used to help with damage calculations
    public static void main(String[] args) throws FileNotFoundException, InterruptedException { //The main method which will hold the actual battle
        int chances = 2;
        int rivalchances = 2;
        File moves = new File ("Database_Move");
        File pokemonlist = new File ("Database_Pokemon");

        try{
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to the Random Pokemon Battle Simulator");
            TimeUnit.SECONDS.sleep(1);

            System.out.println("Trainer, what is your name?");
            TimeUnit.SECONDS.sleep(1);

            String name = scanner.nextLine();
            System.out.println("And what will your rivals name be?");
            String rivalname = scanner.nextLine();
            TimeUnit.SECONDS.sleep(1);


            System.out.println("Your first Pokemon will now be selected. ");
            String[] Pokemon = Pokemonselection(chances, pokemonlist);
            TimeUnit.SECONDS.sleep(1);
            System.out.println("Your Pokmeon is "+Pokemon[0]);

            String [][] movelist = Moveselection(moves, Pokemon);

            String[] OPokemon = Pokemonselection(rivalchances, pokemonlist);

            String [][] Omovelist = Moveselection(moves, OPokemon);

            Random random = new Random();
            int level = random.nextInt(11) + 80; //A random level will be generated, which will influence stats of the pokemon

            random = new Random();
            int Olevel = random.nextInt(11)+80;

            statconfig(Pokemon, level);
            Ostatconfig(OPokemon,Olevel);

            System.out.println("Trainer "+ name +" sent out "+Pokemon[0]);
            TimeUnit.SECONDS.sleep(1);

            System.out.println("The opposing trainer sent out " + OPokemon[0]);
            TimeUnit.SECONDS.sleep(1);


            while (chances >= 0 && rivalchances >= 0){
                String[] currentmove = null;
                Scanner scan = new Scanner (System.in);

                System.out.println(Pokemon[0]+": "+ hp + "/" + maxhp);
                System.out.println("Opposing " + OPokemon[0]+": "+ ohp + "/" + maxohp);
                TimeUnit.SECONDS.sleep(1);

                System.out.println("What will you do?");
                System.out.println(" [1]: " + movelist[0][0] + " [2]: " + movelist[1][0] + " [3]: " +movelist[2][0] + " [4]: " +movelist[3][0]);
                int answer = scan.nextInt();
                currentmove = movelist[answer-1];

                if (spe == ospe){
                    random = new Random();
                    int randomNumber = random.nextInt(2) + 1;
                    if (randomNumber==1){
                        spe++;
                    }
                    else{
                        ospe++;
                    }
                }

                if(spe>ospe){ //In the scenario that you are faster then your opponent

                    random = new Random();
                    int randomnumber = random.nextInt(3)+1;

                    if ((!flinch) || (Imobility  && randomnumber == 1)|| (!Imobility)){
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println(Pokemon[0] + " used " + currentmove[0]); //Effects like flinch and imobility effect the chances of your poekmon moving

                        double num = damagecalc(currentmove, level, OPokemon[1], OPokemon[2], true, Pokemon, false);
                        num = Math.floor(num);
                        ohp -= (int) num;
                        TimeUnit.SECONDS.sleep(1);

                    }

                    if (ohp<=0){

                        System.out.println("The opposing Pokemon fainted");

                        rivalchances--;
                        if(rivalchances<0){
                            break;
                        }

                        OPokemon = Pokemonselection(rivalchances, pokemonlist);

                        random = new Random();
                        Olevel = random.nextInt(11)+80;

                        Omovelist = Moveselection(moves, OPokemon);

                        Ostatconfig(OPokemon, Olevel);
                        System.out.println("The opposing trainer sent in " + OPokemon[0]);


                    }
                    else if (hp <= 0){

                        System.out.println("Your " + Pokemon[0] + " was knocked out.");
                        chances--;
                        if (chances < 0){
                            break;
                        }

                        Pokemon = Pokemonselection(chances, pokemonlist);

                        random = new Random();
                        level = random.nextInt(11)+80;

                        movelist = Moveselection(moves, Pokemon);

                        statconfig(Pokemon, level);
                        TimeUnit.SECONDS.sleep(1);


                        System.out.println(Pokemon[0] + " was sent out.");
                    }
                    else{

                        random = new Random();
                        randomnumber = random.nextInt(3)+1;

                        if ((!Oflinch) || (OImobility && randomnumber == 1)|| (!OImobility)){

                            String [] Ocurrentmove = Omovelist[enemyAI(Omovelist, Pokemon, Olevel)];

                            System.out.println("The opposing " + OPokemon[0] + " used "+ Ocurrentmove[0]);

                            double odamage = damagecalc(Ocurrentmove, Olevel, Pokemon[1], Pokemon[2], false, OPokemon, false);
                            hp -= (int) Math.floor(odamage);
                            TimeUnit.SECONDS.sleep(1);

                        }

                        if (hp<=0){

                            System.out.println("Your " + Pokemon[0] + " was knocked out.");
                            chances--;
                            if (chances < 0){
                                break;
                            }

                            Pokemon = Pokemonselection(chances, pokemonlist);

                            random = new Random();
                            level = random.nextInt(11)+80;

                            movelist = Moveselection(moves, Pokemon);

                            statconfig(Pokemon, level);

                            System.out.println(Pokemon[0] + " was sent out.");
                        }
                        else if (ohp<=0){

                            System.out.println("The opposing Pokemon fainted");

                            rivalchances--;
                            if(rivalchances<0){
                                break;
                            }
                            OPokemon = Pokemonselection(rivalchances, pokemonlist);

                            random = new Random();
                            Olevel = random.nextInt(11)+80;

                            Omovelist = Moveselection(moves, OPokemon);

                            Ostatconfig(OPokemon, Olevel);


                        }
                    }
                }
                else{
                    random = new Random();
                    int randomnumber = random.nextInt(3)+1;

                    if ((!Oflinch) || (OImobility && randomnumber == 1)|| (!OImobility)){

                        String [] Ocurrentmove = Omovelist[enemyAI(Omovelist, Pokemon, Olevel)];

                        System.out.println("The opposing " + OPokemon[0] + " used "+ Ocurrentmove[0]);

                        double odamage = damagecalc(Ocurrentmove, Olevel, Pokemon[1], Pokemon[2], false, OPokemon, false);
                        hp -= (int) Math.floor(odamage);
                        TimeUnit.SECONDS.sleep(1);

                    }
                    if (hp<=0) {

                        System.out.println("Your " + Pokemon[0] + " was knocked out.");
                        chances--;

                        if (chances < 0){
                            break;
                        }

                        Pokemon = Pokemonselection(chances, pokemonlist);

                        random = new Random();
                        level = random.nextInt(11) + 80;

                        movelist = Moveselection(moves, Pokemon);

                        statconfig(Pokemon, level);
                        TimeUnit.SECONDS.sleep(1);


                        System.out.println(Pokemon[0] + " was sent out.");
                    }
                    else if (ohp<=0){

                        System.out.println("The opposing Pokemon fainted");

                        rivalchances--;
                        if(rivalchances<0){
                            break;
                        }
                        OPokemon = Pokemonselection(rivalchances, pokemonlist);

                        random = new Random();
                        Olevel = random.nextInt(11)+80;

                        Omovelist = Moveselection(moves, OPokemon);

                        Ostatconfig(OPokemon, Olevel);

                        TimeUnit.SECONDS.sleep(1);

                        System.out.println("The opposing trainer sent out" + OPokemon[0]);
                    }
                    else{

                        random = new Random();
                        randomnumber = random.nextInt(3)+1;

                        if ((flinch!=true) || (Imobility == true && randomnumber == 1)|| (Imobility== false)){
                            TimeUnit.SECONDS.sleep(1);

                            System.out.println(Pokemon[0] + " used " + currentmove[0]);

                            double num = damagecalc(currentmove, level, OPokemon[1], OPokemon[2], true, Pokemon, false);
                            num = Math.floor(num);
                            ohp -= (int) num;
                            TimeUnit.SECONDS.sleep(1);

                        }

                        if (ohp<=0){
                            System.out.println("The opposing Pokemon fainted");


                            rivalchances--;
                            if(rivalchances<0){
                                break;
                            }
                            OPokemon = Pokemonselection(rivalchances, pokemonlist);

                            random = new Random();
                            Olevel = random.nextInt(11)+80;

                            Omovelist = Moveselection(moves, OPokemon);

                            Ostatconfig(OPokemon, Olevel);


                        }
                        else if (hp <= 0){
                            System.out.println("Your " + Pokemon[0] + " was knocked out.");
                            chances--;
                            if (chances < 0){
                                break;
                            }

                            Pokemon = Pokemonselection(chances, pokemonlist);

                            random = new Random();
                            level = random.nextInt(11)+80;

                            movelist = Moveselection(moves, Pokemon);

                            statconfig(Pokemon, level);

                            System.out.println(Pokemon[0] + " was sent out.");
                        }
                    }
                }
                flinch = false;
                Oflinch = false;
                TimeUnit.SECONDS.sleep(1);

                if (DamageOverTime == true && hp>0){
                    hp-= maxhp/12;
                    System.out.println("Your pokemon took some damage");
                }

                else if (ODamageOverTime == true && ohp>0) {
                    ohp-= maxohp/12;
                    System.out.println("The opposing Pokemon took some damage");
                }

                if (hp<=0){
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("Your " + Pokemon[0] + " was knocked out.");
                    chances--;

                    if (chances < 0){
                        break;
                    }

                    Pokemon = Pokemonselection(chances, pokemonlist);

                    random = new Random();
                    level = random.nextInt(11)+80;

                    movelist = Moveselection(moves, Pokemon);

                    statconfig(Pokemon, level);
                    TimeUnit.SECONDS.sleep(1);


                    System.out.println(Pokemon[0] + " was sent out.");
                }
                else if (ohp<=0){
                    TimeUnit.SECONDS.sleep(1);

                    System.out.println("The opposing Pokemon fainted");
                    if(rivalchances<0){
                        break;
                    }

                    rivalchances--;
                    OPokemon = Pokemonselection(rivalchances, pokemonlist);

                    random = new Random();
                    Olevel = random.nextInt(11)+80;

                    Omovelist = Moveselection(moves, OPokemon);

                    Ostatconfig(OPokemon, Olevel);
                    TimeUnit.SECONDS.sleep(1);

                    System.out.println("Your rival sent out "+ OPokemon);
                }
            }

            if (chances <= -1){
                TimeUnit.SECONDS.sleep(1);

                System.out.println("Rival " + rivalname + " won!");
            }
            else{
                TimeUnit.SECONDS.sleep(1);

                System.out.println("Congratulations! Trainer "+name+" won!");
            }

        }


        catch(FileNotFoundException e){
            System.out.println("Error");
        }

    }

    public static String[] Pokemonselection(int chances, File file) throws FileNotFoundException {
        //This method selects pokemon based on the file Database_Pokemon
        String[] Pokemon = new String[9];

        if (chances >= 0) {
            Random random = new Random();
            int randomnum = random.nextInt(48) + 1;
            randomnum *= 9;
            randomnum -= 8;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                int lineCount = 0;
                String line;

                while (lineCount < randomnum - 1) {
                    reader.readLine();
                    lineCount++;
                }

                int x = 0;
                while (x < 9 && (line = reader.readLine()) != null) {
                    Pokemon[x] = line;
                    x++;
                }
            } catch (IOException e) {
                System.out.println("Error");
            }
        }
        return Pokemon;
    }

    public static String[][] Moveselection (File file, String[] Pokemon) throws FileNotFoundException{
        //The use of multi
        String[][] moves = {{null, null, null, "0", "0", null}, {null, null, null, "0", "0", null}
                , {null, null, null, "0", "0", null}, {null, null, null, "0", "0", null}};
        try {
            //A multidimensional array is needed to store all 4 of the moves and return all of them at once.

            String[] array = readFile();

            int type = 1;
            int store = 0;
            int num = Movetype(Pokemon, type, store);
            store = num;

            num*=6;
            num-=6;

            for (int i = 0; i < 6; i++) {
                moves[0][i] = array[num];
                num++;
            }

            type++;
            num = Movetype(Pokemon, type, store);
            num*=6;
            num-=6;

            for (int i = 0; i < 6; i++) {
                moves[1][i] = array[num];
                num++;
            }

            if (Pokemon[1].equals("Fire") || Pokemon[2].equals("Fire") && !Pokemon[0].equals("Cinderace")
                    && !Pokemon[0].equals("Ceruledge")) { //Certain pokemon will be guranteed to have certain moves, which
                num = 654;
                for (int i = 0; i < 6; i++) {
                    moves[2][i] = array[num];
                    num++;
                }

            } else if (Pokemon[1].equals("Poison") || Pokemon[2].equals("Poison") && !Pokemon[0].equals("Sneasler")) {
                num = 666;
                for (int i = 0; i < 6; i++) {
                    moves[2][i] = array[num];
                    num++;
                }

            } else if (Pokemon[1].equals("Electric") || Pokemon[2].equals("Electric")) {
                num = 660;
                for (int i = 0; i < 6; i++) {
                    moves[2][i] = array[num];
                    num++;
                }

            } else if (Pokemon[1].equals("Ice") || Pokemon[2].equals("Ice")) {
                num = 672;
                for (int i = 0; i < 6; i++) {
                    moves[2][i] = array[num];
                    num++;
                }

            } else if (Pokemon[1].equals("Steel") || Pokemon[2].equals("Steel")) {
                num = 648;
                for (int i = 0; i < 6; i++) {
                    moves[2][i] = array[num];
                    num++;
                }

            } else {
                if (Pokemon[0].equals("Gardevoir") || Pokemon[0].equals("Darkrai") || Pokemon[0].equals("Gengar") ||
                        Pokemon[0].equals("Flutter Mane")) {
                    num = 612;
                    for (int i = 0; i < 6; i++) {
                        moves[2][i] = array[num];
                        num++;
                    }

                } else if (Pokemon[0].equals("Urishifu Multi")) {
                    num = 102;
                    for (int i = 0; i < 6; i++) {
                        moves[2][i] = array[num];
                        num++;
                    }

                } else if (Pokemon[0].equals("Ceruledge")) {
                    num = 48;
                    for (int i = 0; i < 6; i++) {
                        moves[2][i] = array[num];
                        num++;
                    }

                } else if (Pokemon[0].equals("Cinderace")) {
                    num = 42;
                    for (int i = 0; i < 6; i++) {
                        moves[2][i] = array[num];
                        num++;
                    }

                } else if (Pokemon[0].equals("Lucario")) {
                    num = 540;
                    for (int i = 0; i < 6; i++) {
                        moves[2][i] = array[num];
                        num++;
                    }

                } else if (Pokemon[0].equals("Scizor") || Pokemon[0].equals("Sneasler") || Pokemon[0].equals("Crustle") || Pokemon[0].equals("Abomasnow")) {
                    num = 618;
                    for (int i = 0; i < 6; i++) {
                        moves[2][i] = array[num];
                        num++;
                    }
                } else {

                    Random random = new Random();
                    int randomnum = random.nextInt(+5) + 1;
                    if (randomnum == 1) {
                        num = 624;
                        for (int i = 0; i < 6; i++) {
                            moves[2][i] = array[num];
                            num++;
                        }

                    } else if (randomnum == 2) {
                        num = 630;
                        for (int i = 0; i < 6; i++) {
                            moves[2][i] = array[num];
                            num++;
                        }

                    } else if (randomnum == 3) {
                        num = 636;
                        for (int i = 0; i < 6; i++) {
                            moves[2][i] = array[num];
                            num++;
                        }

                    } else if (randomnum == 4) {
                        num = 642;
                        for (int i = 0; i < 6; i++) {
                            moves[2][i] = array[num];
                            num++;
                        }

                    } else {
                        num = 678;
                        for (int i = 0; i < 6; i++) {
                            moves[2][i] = array[num];
                            num++;
                        }
                    }
                }
            }

            if (Pokemon[0].equals("Excadrill") || Pokemon[0].equals("Ursaring") || Pokemon[0].equals("Machamp") || Pokemon.equals("Trevanent")
                    || Pokemon.equals("Ursaluna") || Pokemon.equals("Mamoswine")) {

                num = 192;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Blaziken") || Pokemon[0].equals("Sneasler") || Pokemon[0].equals("Chestnaught")) {
                num = 606;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Darkrai") || Pokemon[0].equals("Gengar") || Pokemon[0].equals("Primarina")
                    || Pokemon[0].equals("Ninetails - Alola") || Pokemon[0].equals("Flutter Mane")) {
                num = 486;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Garchomp") || Pokemon[0].equals("Arcanine") || Pokemon[0].equals("Gyarados")
                    || Pokemon[0].equals("Steelix")) {
                num = 378;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Dragonite") || Pokemon[0].equals("Espeon")) {
                num = 438;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Alakazam") || Pokemon[0].equals("Gardevoir")) {
                num = 594;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Blastoise")) {
                num = 258;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Scizor")) {
                num = 552;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Dracozolt") || Pokemon[0].equals("Venusaur") || Pokemon[0].equals("Tyranitar") ||
                    Pokemon[0].equals("Aerodactyl") || Pokemon[0].equals("Abomasnow") || Pokemon[0].equals("Crustle")) {
                num = 216;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Chien Pao") || Pokemon[0].equals("Tyrantrum") || Pokemon[0].equals("Sharpedo")) {
                num = 492;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Magnezone") || Pokemon[0].equals("Iron Bundle") || Pokemon[0].equals("Porgon-Z")) {
                num = 684;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Snorlax") || Pokemon[0].equals("Urishifu Multi") || Pokemon[0].equals("Lucario")) {
                num = 510;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Accelegor")) {
                num = 72;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Galvantula") || Pokemon[0].equals("Jolteon") || Pokemon[0].equals("Cinderace")) {
                num = 108;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else if (Pokemon[0].equals("Charizard") || Pokemon[0].equals("Ceruledge") || Pokemon[0].equals("Christian's Holy Magikarp")) {
                num = 318;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }

            } else {
                num = 66;
                for (int i = 0; i < 6; i++) {
                    moves[3][i] = array[num];
                    num++;
                }
            }
        }

        catch(FileNotFoundException e) {
            System.out.println("Error");
        }

        return moves;
    }
    public static int Movetype (String[] Pokemon, int type, int store ){ //Deciding move based on pokemons primary types

        String search = Pokemon[type];
        int x = 0;
        Random random = new Random();

        if (search.equals("Fire")){
            x = random.nextInt(7) + 1;
        }

        else if (search.equals("Water")){
            x = random.nextInt(8)+10;
        }

        else if (search.equals("Grass")){
            x = random.nextInt(6)+19;
        }

        else if (search.equals("Flying")){
            x = random.nextInt(4)+24;
        }

        else if (search.equals("Normal")){
            x = random.nextInt(4)+28;
        }

        else if (search.equals("Rock")){
            x = random.nextInt(4)+32;
        }

        else if (search.equals("Ground")){
            x = random.nextInt(6)+36;
        }

        else if (search.equals("Ice")){
            x = random.nextInt(7)+42;
        }

        else if (search.equals("Poison")){
            x = random.nextInt(5)+49;
        }

        else if (search.equals("Dragon")){
            x = random.nextInt(6)+54;
        }

        else if (search.equals("Fairy")){
            x = random.nextInt(4)+60;
        }

        else if (search.equals("Dark")){
            x = random.nextInt(6)+64;
        }

        else if (search.equals("Electric")){
            x = random.nextInt(6)+70;
        }

        else if (search.equals("Steel")){
            x = random.nextInt(5)+76;
        }

        else if (search.equals("Psychic")){
            x = random.nextInt(6)+81;
        }

        else if (search.equals("Bug")){
            x = random.nextInt(4)+87;
        }

        else if (search.equals("Fighting")){
            x = random.nextInt(8)+92;
        }

        else if (search.equals("Ghost")){
            x = random.nextInt(3)+100;
        }

        else{
            type--;
            x = Movetype(Pokemon, type, store);
        }
        if (x==store){
            x = Movetype(Pokemon, type, store);
        }


        return x;
    }
    private static String[] readFile() throws FileNotFoundException {

        File file = new File("Database_Move");
        Scanner scanner = new Scanner(file);

        int linesInFile = countLinesInFile(file);
        String[] array = new String[linesInFile];

        int index = 0;

        while (scanner.hasNextLine()) {
            array[index] = scanner.nextLine();
            index++;
        }

        scanner.close();
        return array;
    }

    private static int countLinesInFile(File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file);
        int count = 0;

        while (scanner.hasNextLine()) {
            scanner.nextLine();
            count++;
        }

        scanner.close();
        return count;
    }
    public static int ingameHP(int base, int level){ //These methods are needed to convert base stats into usable stats
        double stat = 2*base;
        stat += 52;
        stat *= level;
        stat/=100;
        stat+=level;
        stat+=10;
        return (int)Math.floor(stat);
    }
    public static int ingamestat (int base, int level){
        double stat = 2*base;
        stat +=52;
        stat*=level;
        stat /=100;
        stat+=5;
        stat = Math.floor(stat);
        int result = (int) stat;
        return result;
    }
    public static void statconfig (String[] Pokemon, int level){
        int health = Integer.parseInt(Pokemon[3]);
        hp = ingameHP(health,level);

        int attack = Integer.parseInt(Pokemon[4]);
        atk = ingamestat(attack, level);

        int defense = Integer.parseInt(Pokemon[5]);
        def = ingamestat(defense, level);

        int speciala = Integer.parseInt(Pokemon[6]);
        spa = ingamestat(speciala, level);

        int speciald = Integer.parseInt(Pokemon[7]);
        spd = ingamestat(speciald, level);

        int speed = Integer.parseInt(Pokemon[8]);
        spe = ingamestat(speed, level);

        maxhp = hp;
        DamageOverTime = false;
    }
    public static void Ostatconfig (String[] Pokemon, int level){
        int health = Integer.parseInt(Pokemon[3]);
        ohp = ingameHP(health,level);

        int attack = Integer.parseInt(Pokemon[4]);
        oatk = ingamestat(attack, level);

        int defense = Integer.parseInt(Pokemon[5]);
        odef = ingamestat(defense, level);

        int speciala = Integer.parseInt(Pokemon[6]);
        ospa = ingamestat(speciala, level);

        int speciald = Integer.parseInt(Pokemon[7]);
        ospd = ingamestat(speciald, level);

        int speed = Integer.parseInt(Pokemon[8]);
        ospe = ingamestat(speed, level);

        maxohp = ohp;
        ODamageOverTime = false;
    }

    public static double damagecalc (String[] move, int level, String DefendingType, String SecondType, boolean target, String []Pokemon, boolean AI){

        Random random = new Random();
        int accuracy = random.nextInt(100)+1;
        int HitChance = Integer.parseInt(move[4]);
        if (accuracy>HitChance&&AI==false){
            System.out.println("The move missed");
            return 0;
        }

        int power =  Integer.parseInt(move[3]);
        if (power==0){
            statuseffect(target, move, AI );
            return 0;
        }

        double damage = (2*level)/5+2;
        damage *= power;

        if (move[3].equals("P")&&target==true) {
            damage *= (atk / odef);
        }

        else if (move[3].equals("S")&&target==true){
            damage *= (spa/ospd);
        }

        else if (move[3].equals("P")&&target==false) {
            damage *= (oatk / def);
        }

        else if (move[3].equals("S")&&target==false){
            damage *= (ospa/spd);
        }

        damage/=50;
        damage+=2;


        // damage roller
        random = new Random();
        double randomNumber = random.nextDouble() * (1 - 0.85) + 0.85;
        damage *= randomNumber;

        //Type chart
        double typing = 1;
        typing *= weakness(move[1], DefendingType);
        typing *= weakness(move[1], SecondType);
        damage *= typing;

        if (typing >= 2 && AI==false){
            System.out.println("It was super effective!!");
        }
        else if (typing <= 0.5 && AI==false){
            System.out.println("It was not very effective");
        }
        damage *= typing;

        //Critical hit
        random = new Random();
        int ran = random.nextInt(12) + 1;

        if (ran == 12 && AI==false) {
            damage *= 1.5;
            System.out.println("It was a critical hit!");
        }

        //STAB, if the pokemon uses a move that shares it typing, it will gain the same type attack bonus, or stab
        if (move[1].equals(Pokemon[1])||move[1].equals(Pokemon[2])){
            damage *= 1.5;
        }

        damage = Math.floor(damage);
        x = (int) damage;
        statuseffect(target, move, AI);
        return damage;
    }
    public static double weakness (String atype, String dtype){
        double result = 1;

        // Commented type is the attacking type mentioned is the attacking type */
        /* Order of the types listed: Normal 0| Fire 1| Water 2| Electric 3| Grass 4| Ice 5| Fighting 6|
        Poison 7| Ground 8| Flying 9| Psychic 10| Bug 11| Rock 12| Ghost 13| Dragon 14| Dark 15| Steel 16| Fairy 17|
         */
        String [] Types = {"Normal", "Fire", "Water", "Electric", "Grass", "Ice", "Fighting", "Poison","Ground",
        "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy", "Null"};


        int atk = 0;
        int def = 0;

        for (int i =0; i<Types.length; i++){
            if (Types[i].equals(atype)){
                atk = i;
            } else if (Types[i].equals(dtype)){
                def = i;
            }
        }

        double[][] typechart = {/*Normal attacking*/{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5,  0.0, 1.0, 1.0, 0.5, 1.0, 1.0},

        /*Fire*/ {1.0, 0.5, 0.5, 1.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 0.5, 1.0, 0.5, 1.0, 2.0, 1.0, 1.0},

        /*Water*/{1.0, 2.0, 0.5, 1.0, 0.5, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 2.0,  1.0, 0.5, 1.0, 1.0, 1.0, 1.0},

        /*Electric*/{1.0, 1.0, 2.0, 0.5, 0.5, 1.0, 1.0, 1.0, 0.0, 2.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0},

        /*Grass*/{1.0, 0.5, 2.0, 1.0, 0.5, 1.0, 1.0, 0.5, 2.0, 0.5, 1.0, 0.5, 2.0, 1.0, 0.5, 1.0, 0.5, 1.0, 1.0},

        /*Ice*/{1.0, 0.5, 0.5, 1.0, 2.0, 0.5, 1.0, 1.0, 2.0, 2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 0.5, 1.0, 1.0},

        /*Fighting*/{2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 0.5, 1.0, 0.5, 0.5, 0.5, 2.0, 0.0, 1.0, 2.0, 2.0, 0.5, 1.0},

        /*Poison*/ {1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 0.5, 0.5, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 0.0, 2.0, 1.0},

        /*Ground*/{1.0, 2.0, 1.0, 2.0, 0.5, 1.0, 1.0, 2.0, 1.0, 0.0, 1.0, 0.5, 2.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0},

        /*Flying*/{1.0, 1.0, 1.0, 0.5, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 2.0, 0.5, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0},

        /*Psychic*/{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 0.0, 0.5, 1.0, 1.0},

        /*Bug*/ {1.0, 0.5, 1.0, 1.0, 2.0, 1.0, 0.5, 0.5, 1.0, 0.5, 2.0, 1.0, 1.0, 0.5, 1.0, 2.0, 0.5, 0.5, 1.0},

        /*Rock*/{1.0, 2.0, 1.0, 1.0, 1.0, 2.0, 0.5, 1.0, 0.5, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0},

        /*Ghost*/{0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 2.0, 1.0, 0.5, 1.0, 1.0, 1.0},

        /*Dragon*/{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 0.5, 0.0, 1.0},

        /*Dark*/{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 2.0, 1.0, 0.5, 1.0, 0.5, 1.0},

        /*Steel*/{ 1.0, 0.5, 0.5, 0.5, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0,  1.0, 1.0, 2.0, 1.0, 1.0, 1.0, 0.5, 2.0, 1.0},

        /*Fairy*/{ 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 2.0, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 0.5, 1.0, 1.0}
        };

        /*This multidimensional array works like a table. The attacking type as columns, and the defending type as the rows.
        If you wanted to find a bug attack against a fire pokemon, you would go to the bug row, then go to the index fire is at
        based on the Types array above this one.*/

        result = typechart[atk][def];
        return result;
    }

    public static void statuseffect (boolean target/*True is your attacking, false is opponent attacking*/, String[] move, boolean AI){
        if (AI==true){
            return;
        }
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1;

        int power = Integer.parseInt(move[3]);

        if (power != 0){
            if (randomNumber<80){
                return;
            }
        }


        //Status effects list, DOSPE, DOT, Freeze, DOSPD, BSPA, Flinch, Recoil, DSPA, Multi, DOATK
        if (move[5].equals("DOSPE")){  //This stands for Drop opponents speed stat

            if (target==true){
                ospe *= 0.75;
                System.out.println("The opponents speed was dropped");
            }

            else{
                spe *= 0.75;
                System.out.println("Your speed was dropped");
            }
        }

        if (move[5].equals("DOT")){ //DOT stands for damage over time, think of it as a poison effect
            if (target==true&&OImobility==false){
                ODamageOverTime = true;
                System.out.println("The opposing Pokemon now takes damage over time.");
            }
            else if (target==false&&Imobility==false){
                DamageOverTime = true;
                System.out.println("Your Pokemon now takes damage over time.");

            }
        }
        if (move[5].equals("Freeze")||move[5].equals("Para")){ //Freeze + paraylsis give the
            // immobility effect, which gives you a chance of being unable to move
            if (target==true||ODamageOverTime==false){
                OImobility = true;
                System.out.println("The opposing Pokemon will now be immobilised");
            }
            else if (target==false && DamageOverTime == false){
                Imobility = true;
                System.out.println("Your pokemon is now immobilised");
            }
        }

        if (move[5].equals("DOSPD")){ //This stands for drop opponents special defense
            if (target==true){
                ospd*=0.75;
                System.out.println("The opponents special defense was dropped");
            }
            else{
                spd*=0.75;
                System.out.println(("Your pokemons special defense was dropped"));
            }
        }
        if (move[5].equals("BSPA")){
            if (target==true){
                spa *= 1.25;
                System.out.println("Your Pokemons special attack increased");
            }
            else{
                ospa *= 1.25;
                System.out.println("The opponents special attack increased");
            }
        }
        if (move[5].equals("Flinch")){
            if (target==true){
                Oflinch = true;

            }
            else{
                flinch = true;
            }
        }
        if (move[5].equals("Recoil")){ //Some moves give Pokemon recoil effects.
            // The recoil effect will deal 1/12 of the damage you dealt back to you.
            int recoil = x/3;
            if (target==true){
                hp -= recoil;
                System.out.println("Your Pokemon took some recoil damage");
            }
            else{
                ohp-= recoil;
                System.out.println("The opposing Pokemon took some recoil damage");
            }
        }

        if (move[5].equals("DSPA")){//This stands for Drop special attack.
            // Your are dropping your own attack, not the opponents
            if (target == true){
                spa *= 0.75;
                System.out.println("Your special attack stat was lowered");
            }
            else{
                ospa*=0.75;
                System.out.println("The opponents special attack stat was lowered");
            }
        }
        if (move[5].equals("DOATK")){ //This stands for drop opponents attack
            if (target == true){
                oatk *= 0.75;
                System.out.println("The opponents attack was lowered");
            }
            else{
                atk *= 0.75;
                System.out.println("Your Pokemon's attack was lowered");
            }
        }
        Random randint = new Random();
        if (move[5].equals("DDEF")){ //this stands for drop users  defense stat

            if(target==true){
                def*=0.75;
                System.out.println("Your pokemons defense was dropped");
            }

            else{
                odef*=0.75;
                System.out.println("The opposing Pokemons defense was dropped");
            }
        }
        if (move[5].equals("BDEF")){ //this stands for boost users  defense stat

            if(target==true){
                def*=1.25;
                System.out.println("Your pokemons defense was increased");
            }

            else{
                odef*=1.25;
                System.out.println("The opposing Pokemons defense was increased");
            }
        }
        if (move[5].equals("BATK")){ //this stands for boost users  defense stat

            if(target==true){
                atk*=1.25;
                System.out.println("Your pokemons attack was increased");
            }

            else{
                oatk*=1.25;
                System.out.println("The opposing Pokemons attack was increased");
            }
        }
    }

    public static int enemyAI(String[][] movelist, String[] Pokemon, int olevel){
        //This method is needed, as I want the AI to priorities the move that does the most damage,
        // while still being random
        double first = damagecalc(movelist[0], olevel, Pokemon[1], Pokemon[2], false, Pokemon, true);
        double second = damagecalc(movelist[1], olevel, Pokemon[1], Pokemon[2], false, Pokemon, true);
        double third = damagecalc(movelist[3], olevel, Pokemon[1], Pokemon[2], false, Pokemon, true);

        double[] list = {first, second, third};
        double storage = first;
        for(int i =0; i<list.length; i++){
            if (list[i]>first){
                storage = list[i];
            }
        }

        Random random = new Random();
        int randomnumber = random.nextInt(10)+1;
        if (randomnumber<=4){ //Most damaging move has a 40% chance of occuring
            if (storage == first){
                return 0;
            }
            else if (storage == second){
                return 1;
            }
            else{
                return 3;
            }
        }

        else if (randomnumber>4&&randomnumber<=6){ //Other 3 moves have a 20% chance
            if (storage == first){
                return 1;
            }
            else if (storage == second){
                return 3;
            }
            else{
                return 0;
            }
        }

        else if (randomnumber>6&&randomnumber<=8){
            if (storage == first){
                return 3;
            }
            else if (storage == second){
                return 0;
            }
            else{
                return 1;
            }
        }
        else {
            return 2;
        }
    }
}