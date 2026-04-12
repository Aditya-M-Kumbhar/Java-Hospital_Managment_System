# Hospital Management System v3.0

## How to Compile & Run

### GUI Only (Recommended)
```cmd
javac -d out src/hospital/exception/*.java src/hospital/model/*.java src/hospital/repository/*.java src/hospital/service/*.java src/hospital/gui/*.java src/hospital/main/*.java

java -cp out hospital.gui.HospitalGUI
```

### CLI Only
```cmd
javac -d out src/hospital/exception/*.java src/hospital/model/*.java src/hospital/repository/*.java src/hospital/service/*.java src/hospital/gui/*.java src/hospital/main/*.java

java -cp out hospital.main.Main
```

From the main menu, you can also select **Option 6: Launch GUI** to open the GUI interface.

## Features
- 10 patients + 5 doctors pre-loaded on startup
- Appointment slots: 09:00–13:00 and 15:00–19:00 (every 30 mins)
- Lunch break: 13:00–15:00 (no slots)
- Booking allowed only for next 3 days
- Doctor surgery/leave blocking (must schedule 2+ days ahead)
- Show available/booked slots per doctor per day
- Concurrent Booking Simulation (Main Menu → Option 5)

## GUI Features (Java Swing)

A complete graphical interface with 5 tabs:

| Tab | Features |
|---|---|
| **Patients** | Add patients with ID/Name/Phone; View all patients in table |
| **Doctors** | Add doctors with specialization status; Update doctor status (AVAILABLE/IN_SURGERY/ON_LEAVE); Block surgery dates; View blocked dates |
| **Appointments** | Book appointments with real-time slot validation; Cancel/Mark completed; Filter by status (SCHEDULED/CANCELLED/COMPLETED); View available slots for next 3 days; Auto-validation prevents "ghost bookings" with deleted patients/doctors |
| **Billing** | Generate bills for patients; View bill details with timestamp |
| **Simulation** | Run concurrent booking simulation (6 simultaneous threads competing for same slot); Real-time output display |

### GUI Safety Features
- Auto-refresh patient/doctor combos before critical operations
- Validates that selected patient/doctor still exists before booking
- Shows error and auto-refreshes list if data was deleted externally
- Prevents booking appointments with non-existent patients/doctors

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

## GUI Architecture (Java Swing)

### Core Components
- **HospitalGUI**: Main window with tabbed interface (JTabbedPane)
- **PatientPanel**: JTable for patient CRUD, BoxLayout for clean UI alignment
- **DoctorPanel**: Doctor management with status updates and date blocking
- **AppointmentPanel**: Appointment booking with real-time validation
- **BillingPanel**: Bill generation and viewing
- **SimulationPanel**: Concurrent booking demo with real-time output

### GUI Design Patterns
- **MVC Pattern**: All panels interact with shared `HospitalSystem` backend (Singleton)
- **BoxLayout + FlowLayout**: Proper label-field alignment preventing UI issues
- **Thread Safety**: Uses existing `ReentrantLock` from backend for concurrent operations
- **Auto-Refresh**: Combos refresh before critical operations to catch data changes
- **Error Handling**: Comprehensive validation with user-friendly error dialogs

## Package Structure
```
src/hospital/
├── main/        Main.java
├── model/       Person, Patient, Doctor, Appointment, Bill,
│                AppointmentStatus, DoctorStatus
├── service/     Schedulable, AppointmentService, HospitalSystem,
│                SlotManager, DataLoader, ConcurrentBookingSimulator
├── repository/  Repository<T>
├── gui/         HospitalGUI (Main window)
│                PatientPanel, DoctorPanel, AppointmentPanel,
│                BillingPanel, SimulationPanel
└── exception/   HospitalException, AppointmentConflictException,
                 EntityNotFoundException
```
