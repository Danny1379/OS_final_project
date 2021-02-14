import java.util.concurrent.Semaphore;
import java.util.Scanner ;


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
