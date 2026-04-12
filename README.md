# Hospital Management System v3.0

## How to Compile & Run

```cmd
javac -d out src/hospital/exception/*.java src/hospital/model/*.java src/hospital/repository/*.java src/hospital/service/*.java src/hospital/main/*.java

java -cp out hospital.main.Main
```

## Features
- 10 patients + 5 doctors pre-loaded on startup
- Appointment slots: 09:00–13:00 and 15:00–19:00 (every 30 mins)
- Lunch break: 13:00–15:00 (no slots)
- Booking allowed only for next 3 days
- Doctor surgery/leave blocking (must schedule 2+ days ahead)
- Show available/booked slots per doctor per day
- 🔴 Concurrent Booking Simulation (Main Menu → Option 5)

## Java Concepts Used
| Concept | Location |
|---|---|
| Abstract Class | Person → Patient, Doctor |
| Interface | Schedulable → AppointmentService |
| Generics | Repository<T extends Person> |
| Enum (x2) | AppointmentStatus, DoctorStatus |
| Optional | Repository.findById() |
| Custom Exception Hierarchy | HospitalException → sub-exceptions |
| Singleton + volatile | HospitalSystem (double-checked locking) |
| ReentrantLock (fair) | AppointmentService.bookAppointment() |
| synchronized | Doctor.blockDate() / isBlockedOn() |
| CountDownLatch | ConcurrentBookingSimulator |
| volatile field | Appointment.status, Doctor.status |
| Streams + Lambda | Throughout |
| Method References | forEach(Doctor::displayDetails) etc. |
| LocalDate / LocalTime | Real date-time validation |

## Package Structure
```
src/hospital/
├── main/        Main.java
├── model/       Person, Patient, Doctor, Appointment, Bill,
│                AppointmentStatus, DoctorStatus
├── service/     Schedulable, AppointmentService, HospitalSystem,
│                SlotManager, DataLoader, ConcurrentBookingSimulator
├── repository/  Repository<T>
└── exception/   HospitalException, AppointmentConflictException,
                 EntityNotFoundException
```
