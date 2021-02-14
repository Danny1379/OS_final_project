import java.util.concurrent.Semaphore;

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