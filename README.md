<<<<<<< HEAD
# Hospital Management System v2.0

## Java Concepts Demonstrated
| Concept | Where Used |
|---|---|
| Abstract Class | `Person` → `Patient`, `Doctor` |
| Interface | `Schedulable` → `AppointmentService` |
| Generics | `Repository<T extends Person>` |
| Enum | `AppointmentStatus` (SCHEDULED, COMPLETED, CANCELLED) |
| Optional | `Repository.findById()` — null-free lookups |
| Custom Exception Hierarchy | `HospitalException` → `AppointmentConflictException`, `EntityNotFoundException` |
| Singleton (thread-safe) | `HospitalSystem` — double-checked locking + `volatile` |
| Multithreading / ReentrantLock | `AppointmentService` — prevents double-booking under concurrency |
| `volatile` field | `Appointment.status` — cross-thread visibility |
| Streams + Lambda | Throughout — filtering, searching, displaying |
| Method References | `forEach(Patient::displayDetails)` etc. |
| `LocalDate` / `LocalTime` | Real date-time handling in `Appointment` |
=======
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
>>>>>>> b1a3c5869d90701868141436b4159b625ac4390c

## Package Structure
```
src/hospital/
<<<<<<< HEAD
├── main/       Main.java
├── model/      Person, Patient, Doctor, Appointment, Bill, AppointmentStatus
├── service/    Schedulable, AppointmentService, HospitalSystem
├── repository/ Repository<T>
└── exception/  HospitalException, AppointmentConflictException, EntityNotFoundException
```

## Compile & Run
```bash
javac -d out $(find src -name "*.java")
java -cp out hospital.main.Main
```

## Real-Life Problem Solved
Two patients booking the same doctor slot at the same time → `ReentrantLock` 
ensures only one succeeds; the other gets a clear conflict error.
=======
├── main/        Main.java
├── model/       Person, Patient, Doctor, Appointment, Bill,
│                AppointmentStatus, DoctorStatus
├── service/     Schedulable, AppointmentService, HospitalSystem,
│                SlotManager, DataLoader, ConcurrentBookingSimulator
├── repository/  Repository<T>
└── exception/   HospitalException, AppointmentConflictException,
                 EntityNotFoundException
```
>>>>>>> b1a3c5869d90701868141436b4159b625ac4390c
