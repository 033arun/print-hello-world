import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple desktop AI-style timetable generator with a Google-like search interface.
 */
public class AiTimetableApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("AI Timetable Assistant");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(920, 620);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new SearchPanel());
            frame.setVisible(true);
        });
    }

    static class SearchPanel extends JPanel {
        private final JTextField inputField = new JTextField();
        private final JTextArea outputArea = new JTextArea();

        SearchPanel() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            JPanel top = new JPanel();
            top.setBackground(Color.WHITE);
            top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
            top.setBorder(new EmptyBorder(40, 90, 20, 90));

            JLabel logo = new JLabel("TimeAI");
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            logo.setFont(new Font("SansSerif", Font.BOLD, 52));
            logo.setForeground(new Color(66, 133, 244));

            JPanel searchRow = new JPanel(new BorderLayout(8, 0));
            searchRow.setBackground(Color.WHITE);
            searchRow.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                    new EmptyBorder(10, 12, 10, 12)
            ));

            inputField.setBorder(BorderFactory.createEmptyBorder());
            inputField.setFont(new Font("SansSerif", Font.PLAIN, 18));
            inputField.setText("Create timetable for a housewife");

            JButton askButton = new JButton("Ask AI");
            askButton.setFocusPainted(false);
            askButton.setBackground(new Color(241, 243, 244));

            searchRow.add(inputField, BorderLayout.CENTER);
            searchRow.add(askButton, BorderLayout.EAST);

            top.add(logo);
            top.add(Box.createRigidArea(new Dimension(0, 24)));
            top.add(searchRow);

            outputArea.setEditable(false);
            outputArea.setLineWrap(true);
            outputArea.setWrapStyleWord(true);
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
            outputArea.setBorder(new EmptyBorder(16, 16, 16, 16));
            outputArea.setText("Try prompts like:\n"
                    + "• time table for white collar employee\n"
                    + "• make a timetable for mechanic\n"
                    + "• timetable for everyone\n");

            JScrollPane scroll = new JScrollPane(outputArea);
            scroll.setBorder(BorderFactory.createEmptyBorder(8, 90, 20, 90));

            askButton.addActionListener(e -> generate());
            inputField.addActionListener(e -> generate());

            add(top, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
        }

        private void generate() {
            String prompt = inputField.getText().trim();
            if (prompt.isEmpty()) {
                outputArea.setText("Please ask for a timetable.");
                return;
            }

            TimetableGenerator generator = new TimetableGenerator();
            outputArea.setText(generator.respond(prompt));
        }
    }

    static class TimetableGenerator {

        String respond(String prompt) {
            String lower = prompt.toLowerCase();

            boolean asksForEveryone = lower.contains("everyone") || lower.contains("all");
            boolean asksHousewife = lower.contains("housewife") || lower.contains("homemaker");
            boolean asksEmployee = lower.contains("white collar") || lower.contains("employee") || lower.contains("office");
            boolean asksMechanic = lower.contains("mechanic") || lower.contains("workshop");

            StringBuilder reply = new StringBuilder();
            reply.append("AI Timetable Result\n");
            reply.append("Prompt: \"").append(prompt).append("\"\n\n");

            if (asksForEveryone || (!asksHousewife && !asksEmployee && !asksMechanic)) {
                reply.append(buildSection("Housewife / Homemaker", housewifeSchedule()));
                reply.append("\n");
                reply.append(buildSection("White-Collar Employee", employeeSchedule()));
                reply.append("\n");
                reply.append(buildSection("Mechanic", mechanicSchedule()));
            } else {
                if (asksHousewife) {
                    reply.append(buildSection("Housewife / Homemaker", housewifeSchedule()));
                    reply.append("\n");
                }
                if (asksEmployee) {
                    reply.append(buildSection("White-Collar Employee", employeeSchedule()));
                    reply.append("\n");
                }
                if (asksMechanic) {
                    reply.append(buildSection("Mechanic", mechanicSchedule()));
                }
            }

            reply.append("\nTip: You can ask for custom wake-up time or night shift in the next version.");
            return reply.toString();
        }

        private String buildSection(String title, List<Slot> slots) {
            StringBuilder sb = new StringBuilder();
            sb.append(title).append("\n");
            sb.append("-".repeat(title.length())).append("\n");
            for (Slot slot : slots) {
                sb.append(String.format("%s - %s  %s%n",
                        slot.start,
                        slot.end,
                        slot.activity));
            }
            return sb.toString();
        }

        private List<Slot> housewifeSchedule() {
            List<Slot> list = new ArrayList<>();
            list.add(new Slot("06:00", "07:00", "Wake up, hydration, light exercise"));
            list.add(new Slot("07:00", "09:00", "Breakfast prep, family readiness, household setup"));
            list.add(new Slot("09:00", "11:00", "Cleaning and organizing"));
            list.add(new Slot("11:00", "13:00", "Lunch prep and errands"));
            list.add(new Slot("13:00", "14:00", "Lunch + short rest"));
            list.add(new Slot("14:00", "16:00", "Personal development / side project"));
            list.add(new Slot("16:00", "19:00", "Snacks, kids/family support, dinner prep"));
            list.add(new Slot("19:00", "21:00", "Dinner, cleanup, plan next day"));
            list.add(new Slot("21:00", "22:00", "Relaxation / reading"));
            return list;
        }

        private List<Slot> employeeSchedule() {
            List<Slot> list = new ArrayList<>();
            list.add(new Slot("06:30", "07:30", "Wake up, workout, get ready"));
            list.add(new Slot("07:30", "09:00", "Breakfast + commute"));
            list.add(new Slot("09:00", "12:00", "Deep work block"));
            list.add(new Slot("12:00", "13:00", "Lunch + short walk"));
            list.add(new Slot("13:00", "17:30", "Meetings + task execution"));
            list.add(new Slot("17:30", "19:00", "Commute back + unwind"));
            list.add(new Slot("19:00", "21:00", "Family time / learning"));
            list.add(new Slot("21:00", "22:30", "Light planning + sleep prep"));
            return list;
        }

        private List<Slot> mechanicSchedule() {
            List<Slot> list = new ArrayList<>();
            list.add(new Slot("06:00", "07:00", "Wake up, stretching, breakfast"));
            list.add(new Slot("07:00", "08:00", "Workshop opening and tool inspection"));
            list.add(new Slot("08:00", "12:00", "Vehicle diagnosis and priority repairs"));
            list.add(new Slot("12:00", "13:00", "Lunch break"));
            list.add(new Slot("13:00", "17:00", "Repairs, testing, customer updates"));
            list.add(new Slot("17:00", "18:00", "Parts inventory + cleanup"));
            list.add(new Slot("18:00", "20:00", "Family time / hobby / skill upgrade"));
            list.add(new Slot("20:00", "21:30", "Dinner + rest"));
            return list;
        }

        static class Slot {
            final String start;
            final String end;
            final String activity;

            Slot(String start, String end, String activity) {
                validateTime(start);
                validateTime(end);
                this.start = start;
                this.end = end;
                this.activity = activity;
            }

            private static void validateTime(String value) {
                LocalTime.parse(value + ":00");
            }
        }
    }
}
