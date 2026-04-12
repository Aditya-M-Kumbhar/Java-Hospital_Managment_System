package hospital.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates valid appointment slots:
 *  Morning  : 09:00 – 13:00  (30-min slots)
 *  Afternoon: 15:00 – 19:00  (30-min slots)
 *  Break    : 13:00 – 15:00  (no slots)
 */
public class SlotManager {

    private static final LocalTime MORNING_START   = LocalTime.of(9, 0);
    private static final LocalTime MORNING_END     = LocalTime.of(13, 0);
    private static final LocalTime AFTERNOON_START = LocalTime.of(15, 0);
    private static final LocalTime AFTERNOON_END   = LocalTime.of(19, 0);

    public static List<LocalTime> getAllSlots() {
        List<LocalTime> slots = new ArrayList<>();
        addSlots(slots, MORNING_START,   MORNING_END);
        addSlots(slots, AFTERNOON_START, AFTERNOON_END);
        return slots;
    }

    private static void addSlots(List<LocalTime> slots, LocalTime start, LocalTime end) {
        LocalTime t = start;
        while (t.isBefore(end)) {
            slots.add(t);
            t = t.plusMinutes(30);
        }
    }

    public static boolean isValidSlot(LocalTime time) {
        return getAllSlots().contains(time);
    }
}
