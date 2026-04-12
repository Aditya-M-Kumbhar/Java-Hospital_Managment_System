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

## Package Structure
```
src/hospital/
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
