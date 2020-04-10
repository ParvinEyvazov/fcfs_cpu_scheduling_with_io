import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/*
@author ParvinEyvazov
*/



public class main {
    public static HashMap<Integer,ArrayList<ArrayList<Integer>>> myHash = new HashMap<>();
    public static List<Integer> allWorkingTime = new ArrayList<>();
    public static List<Integer> allTurnaroundTime = new ArrayList<>();


    //cmd-veya bash-dan file path-vererek calistirilir
    public static void main(String[] args) throws FileNotFoundException {

        File file = new File(args[0]);


        //////////////////////////////////////Fill THE HASHMAP/////////////////////////////////////////////////

        //path of jobs.txt file

        Scanner jobs = new Scanner(file);

        //using this int for calculating KEY of the process in the while for assign it in the hash
        int processSayi =0;

        while (jobs.hasNextLine()){ //if has a nextLine in the line return TRUE
            String process = jobs.nextLine();// assign the whole line as a string in a value,and also increase line number(for while) in the LINE
            processSayi++; // for assign-ing key in the hashMap
            allWorkingTime.add(0);
            allTurnaroundTime.add(0);

            //START TO FILL THE VALUE PART
            ArrayList<ArrayList<Integer>> tempProcess = new ArrayList<>(); //ArrayList of current line process

            //add first 0 for the return time
            tempProcess.add(new ArrayList<>() {
                {
                    add(0);
                }
            });

            // add second 0 for the processFinish boolean(0/1)
            tempProcess.add(new ArrayList<>() {
                {
                    add(0);
                }
            });

            //firstly split it in 2parts with the ":"
            String[] firstCut = process.split(":");
            //cut the first number part
            process = firstCut[1];
            process =removeBrackets(process); //bracketsleri remove yapip stringi (45,15);(16,20);(80,10);(40,-1) --> `45,15`16,20`80,10`40,-1 olarak ayarlamak
            process = process.substring(1,process.length()); //ilk bastaki ` isaretini silmek

            String[] tuples = process.split("`");


            //tuplelerdeki degerli teker teker arraylist haline getirip genel arrayliste eklemek
            for(int i =0;i<tuples.length;i++){
                String tempTuple = tuples[i];
                String[] tempArr = tempTuple.split(",");

                //Value olarak ilk 2 0-dan sonra tupleleri cpu,i/o,0 olarak dolduruyoruz
                tempProcess.add(new ArrayList<>() {
                    {
                        add(Integer.parseInt(tempArr[0]));
                        add(Integer.parseInt(tempArr[1]));
                        add(0);
                    }
                });


            }
            myHash.put(processSayi,tempProcess);
        }

        int time = 0;
        int allDoneCount = 0;
        int pro_no;

        System.out.printf("%-10s %-10s %-10s %-10s %-10s\n", "TIME", "C_P", "cpu_b","i/o_b","R_T");
        while (allDoneCount<myHash.size()){

            pro_no = find_process(time);

            if (pro_no != -1){ //herhansi bir gercek process_no donmusse eger
                for (int i=2 ; i< myHash.get(pro_no).size(); i++) { //2den sonraki her eleman (cpu,i/o) islemidir
                    if (myHash.get(pro_no).get(i).get(2) == 0){ //tupledeki 3cu eleman yani isleyip islemedigin gosteren eleman 0ise ,yani o tuple onceden islememisse

                        time = time + myHash.get(pro_no).get(i).get(0); // cpu burst time-i onceki time ile topluyoruz
                        if (myHash.get(pro_no).get(i).get(1) == -1){ //process bitmis ise

                            //return_time-deyis , 3cu valueni deyis, procesesin bitme valuesin deyis
                            int return_time = time; //eski time + cpu_burst_time
                            myHash.get(pro_no).get(0).set(0,return_time); // return_time-i deyisib guncel value yapmak
                            myHash.get(pro_no).get(i).set(2,1); //o procesi bitir
                            //ve en son o proses artik bitti ,2ci valueni deyis
                            myHash.get(pro_no).get(1).set(0,1);

                            System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s\n", time - myHash.get(pro_no).get(i).get(0)  , "P"+pro_no , myHash.get(pro_no).get(i).get(0), myHash.get(pro_no).get(i).get(1) , myHash.get(pro_no).get(0).get(0), "pro over");


                            allTurnaroundTime.set(pro_no-1 , myHash.get(pro_no).get(0).get(0) );// add turnaround time to the arraylist that index is pro_no (pro_no-1)
                            allWorkingTime.set(pro_no-1, allWorkingTime.get(pro_no-1) + myHash.get(pro_no).get(i).get(0)); // add all cpu burst times(working times) to the arraylist that index is pro_no-1
                            allDoneCount++;
                            break;

                        }

                        else {
                            int return_time = time + myHash.get(pro_no).get(i).get(1); //eski time + cpu(burst) + i/o(burst)
                            myHash.get(pro_no).get(0).set(0,return_time); // return_time-i deyisib guncel value yapmak
                            //isletdiyi tuplenin 3cu degerini 1 yapmak
                            myHash.get(pro_no).get(i).set(2,1); //o procesi bitir

                            System.out.printf("%-10s %-10s %-10s %-10s %-10s\n", time - myHash.get(pro_no).get(i).get(0)  , "P"+pro_no , myHash.get(pro_no).get(i).get(0), myHash.get(pro_no).get(i).get(1) , myHash.get(pro_no).get(0).get(0));

                            allWorkingTime.set(pro_no-1, allWorkingTime.get(pro_no-1) + myHash.get(pro_no).get(i).get(0)); // add all cpu burst times(working times) to the arraylist that index is pro_no-1
                            break;
                        }

                    }
                }


            }else {
                time++; //eger hicbir process cpu-da calisamazsa o zaman time-i increment yapip devam ediyor
            }
        }

        showTimes();
        showAverage();
    }

    public static void showAverage(){
        int averageTurnaroundTime = 0;
        int averageWaitingTime    = 0;

        for (int i = 0;i< allTurnaroundTime.size();i++){
            averageTurnaroundTime = averageTurnaroundTime + allTurnaroundTime.get(i);
            averageWaitingTime = averageWaitingTime + (allTurnaroundTime.get(i)-allWorkingTime.get(i));
        }
        System.out.println("Average Turnaround Time : " + averageTurnaroundTime/allTurnaroundTime.size());
        System.out.println("Average Waiting Time : " + averageWaitingTime/allTurnaroundTime.size());
    }


    public static void showTimes() {
        for (int i= 0 ;i<allTurnaroundTime.size();i++){
            System.out.println();
            System.out.println("Turnaround time of process " + (i+1) + " is " + allTurnaroundTime.get(i));
            System.out.println("Waiting time of process " + (i+1) + " is " + (allTurnaroundTime.get(i)-allWorkingTime.get(i)));
            System.out.println();
        }
    }


    public static String removeBrackets(String a){
        a= a.replace(")","");
        a = a.replace(";","");
        a = a.replace("(","`");
        return a;
    }



    public static int find_process(int time){
        //global bir hashmap-imiz var --> myHash

        //hash-in icindenki elemanlari bastan sona gidicek
        for (int i =1; i<=myHash.size();i++){
            if (myHash.get(i).get(1).get(0) == 0){
                //hash map-in key-i i olan elemaninin 1ci
                // arraylistinin(check 0/1) 0-ci elemani 0 ise process bitmemis

                if (myHash.get(i).get(0).get(0) <= time){
                    //eger o procesesin return time-si daha kucuk veya esit ise

                    return i; // i-ci procesesin o an calisa bilir oldugunu dondur
                }
                continue;
            }
            continue;
        }
        return -1;
    }
}

