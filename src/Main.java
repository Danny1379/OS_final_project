import java.util.Calendar;
import java.util.concurrent.Semaphore;
import java.util.Scanner ;
class Doctor extends Thread {
    static int time = 2000;
    Semaphore full ;
    Semaphore[] waiting_room ;
    Semaphore full_pointer_lock ;
    Semaphore available_doctors ;
    int doctor_id ;
    int patient_id;
    Doctor(Semaphore full,Semaphore[] waiting_room,Semaphore full_pointer_lock,Semaphore available_doctors,int doctor_id){
        this.full = full ;
        this.waiting_room = waiting_room ;
        this.full_pointer_lock = full_pointer_lock ;
        this.available_doctors = available_doctors ;
        this.doctor_id = doctor_id ;
    }
    public void run() {
        while (true){
            try {
                full.acquire();

                full_pointer_lock.acquire();
                Main.array_write_doctor[Main.full_pointer].acquire();
                patient_id = Main.array_Seats_patient[Main.full_pointer] ;
                waiting_room[Main.full_pointer].release();
                Main.array_seats_doctor[Main.full_pointer] = doctor_id ;
                System.out.println("Doctor "+doctor_id+" Checking Patient in seat "+Main.full_pointer+" with id "+patient_id);
                Main.full_pointer= (Main.full_pointer+1)% Main.m ;
                full_pointer_lock.release();
                sleep(time);
            }
            catch (Exception exc){
                System.out.println(exc);
            }
        }
    }
}
class Patient extends Thread{
    Semaphore counter_lock ;
    Semaphore[] waiting_room ;
    Semaphore empty_pointer_lock ;
    Semaphore full ;
    Semaphore empty ;
    Semaphore available_doctors ;
    int id ;
    int doctor_id ;
    int entrance_time ;
    Patient(Semaphore counter_lock,Semaphore[] waiting_room,Semaphore empty_pointer_lock,Semaphore full,Semaphore empty,Semaphore available_doctors,int id,int time){
        this.counter_lock = counter_lock ;
        this.waiting_room = waiting_room ;
        this.empty_pointer_lock = empty_pointer_lock ;
        this.full = full ;
        this.empty = empty ;
        this.available_doctors = available_doctors ;
        this.id = id ;
        this.entrance_time = time ;
    }
    @Override
    public void run() {
        try {
            //wait till entrance time is ok
            System.out.println(id + " : " + entrance_time);
            sleep((entrance_time-1)* 1000L);

            // check entrance if its empty
            counter_lock.acquire();
            if(Main.Patients_counter>=Main.m){
                System.out.println("No Room patient "+ id+ " failed to enter");
                counter_lock.release();
            }
            else{
                //go to waiting room
                Main.Patients_counter++ ;
                counter_lock.release();

                //find empty chair
                empty_pointer_lock.acquire();
                Calendar c = Calendar.getInstance();
                System.out.println("Patient: " +id + " entered seat "+ Main.empty_pointer+" at minute "+ c.get(Calendar.MINUTE)+ " and Second " + c.get(Calendar.SECOND));
                int temp = Main.empty_pointer ;
                Main.empty_pointer = (Main.empty_pointer+1)%Main.m ;
                Main.array_Seats_patient[temp] = id ;
                empty_pointer_lock.release();
                // for doctor to call patients
                full.release();
                //wait on seat
                waiting_room[temp].acquire();
                doctor_id = Main.array_seats_doctor[temp] ;
                Main.array_write_doctor[temp].release();
                counter_lock.acquire();
                Main.Patients_counter-- ;
                counter_lock.release();
                sleep(Doctor.time);
                Calendar t = Calendar.getInstance();
                System.out.println("patient :"+id+" finished the visit to doctor "+ doctor_id + " at minute "+ t.get(Calendar.MINUTE)+" and second "+ t.get(Calendar.SECOND));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
public class Main {
    static int Patients_counter = 0 ;
    static Scanner input = new Scanner(System.in) ;
    //Waiting Room Capacity
    static int m ;
    //Doctor threads
    static int n ;
    //points to next empty chair
    static int empty_pointer = 0 ;
    static int full_pointer = 0 ;
    static int[] array_Seats_patient;
    static int[] array_seats_doctor ;
    static Semaphore[] array_write_doctor ;
    static Semaphore doctor_write = new Semaphore(1);
    public static void main(String[] args) {

        //Lock for Patients_counter
        Semaphore counter_lock = new Semaphore(1);
        //Lock for array pointer pointing to first empty element
        Semaphore empty_pointer_lock = new Semaphore(1);
        //Lock for array pointer pointing to first full element
        Semaphore full_pointer_lock = new Semaphore(1) ;
        System.out.println("Please enter the number of doctors:" );
        n = input.nextInt();
        System.out.println("Please enter the number of Seats:");
        m = input.nextInt();
        Semaphore empty = new Semaphore(m);
        //Doctor Waits if there are no Patients
        Semaphore full = new Semaphore(0) ;
        //Seats where Patients can sit and are signaled by doctors to continue
        Semaphore[] waiting_room = new Semaphore[m] ;
        Semaphore available_Doctors = new Semaphore(0);
        array_write_doctor = new Semaphore[m];
        for (int i = 0; i <m ; i++) {
            array_write_doctor[i] = new Semaphore(1) ;
            waiting_room[i] = new Semaphore(0) ;
        }
        Doctor[] doctors = new Doctor[n] ;
        Patient[] in_waiting = new Patient[m] ;
        for (int i = 0; i <n ; i++) {
            doctors[i] = new Doctor(full,waiting_room,full_pointer_lock,available_Doctors,i);
            doctors[i].start() ;
        }
        int i=0 ;
        //see which patient goes to which doctor
        array_Seats_patient = new int[m] ;
        array_seats_doctor = new int[m] ;
//         System.out.println("input 1 for each Patient ");
//        while (1==input.nextInt()){
//            Patient p = new Patient(counter_lock,waiting_room,empty_pointer_lock,full,empty,available_Doctors,i,0);
//            p.start();
//            i++ ;
//        }
        Patient[] patients = new Patient[] {
                new Patient(counter_lock,waiting_room,empty_pointer_lock,full,empty,available_Doctors,1,1),
                new Patient(counter_lock,waiting_room,empty_pointer_lock,full,empty,available_Doctors,2,1),
                new Patient(counter_lock,waiting_room,empty_pointer_lock,full,empty,available_Doctors,3,1),
                new Patient(counter_lock,waiting_room,empty_pointer_lock,full,empty,available_Doctors,4,1),
                new Patient(counter_lock,waiting_room,empty_pointer_lock,full,empty,available_Doctors,5,1),
                new Patient(counter_lock,waiting_room,empty_pointer_lock,full,empty,available_Doctors,6,3),
                new Patient(counter_lock,waiting_room,empty_pointer_lock,full,empty,available_Doctors,7,3),
                new Patient(counter_lock,waiting_room,empty_pointer_lock,full,empty,available_Doctors,8,10)
        };

        for (Patient patient : patients) {
            patient.start();
        }
    }
}
