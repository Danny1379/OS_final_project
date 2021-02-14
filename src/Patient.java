import java.util.Calendar;
import java.util.concurrent.Semaphore;

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