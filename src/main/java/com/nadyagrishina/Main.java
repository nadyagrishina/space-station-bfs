
package com.nadyagrishina;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Main {

    public static void main(String[] args) {
        Map<String, List<String>> graph = Map.of(
                "A", List.of("B"),
                "B", List.of("A", "C", "D"),
                "C", List.of("B"),
                "D", List.of("B", "E"),
                "E", List.of("D")
        );

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Space Station");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 530);
            frame.setLocationRelativeTo(null);

            JPanel content = new JPanel(new BorderLayout(0, 15));
            content.setBorder(new EmptyBorder(20, 20, 20, 20));
            frame.setContentPane(content);

            JComboBox<String> startBox = new JComboBox<>(
                    new String[]{"A", "B", "C", "D", "E"}
            );

            JComboBox<String> corridorBox = new JComboBox<>(
                    new String[]{"A-B", "B-C", "B-D", "D-E"}
            );
            corridorBox.setSelectedItem("B-D");

            JPanel controls = new JPanel(new GridLayout(2, 2, 10, 10));
            controls.add(new JLabel("Starting module:"));
            controls.add(startBox);
            controls.add(new JLabel("Damaged corridor:"));
            controls.add(corridorBox);

            Set<String> reachable = bfs(graph, "A", "B", "D");
            GraphPanel panel = new GraphPanel(graph, reachable);

            JLabel status = new JLabel(
                    "Reachable modules: " + reachable.size() + " z " + graph.size(),
                    SwingConstants.CENTER
            );

            ActionListener update = e -> {
                String start = (String) startBox.getSelectedItem();
                String corridor = (String) corridorBox.getSelectedItem();

                if (start == null || corridor == null) {
                    return;
                }

                String[] modules = corridor.split("-");
                String from = modules[0];
                String to = modules[1];

                Set<String> result = bfs(graph, start, from, to);

                panel.updateGraph(result, start, from, to);
                status.setText(
                        "Reachable modules: " + result.size() + " of " + graph.size()
                );
            };

            startBox.addActionListener(update);
            corridorBox.addActionListener(update);

            content.add(controls, BorderLayout.NORTH);
            content.add(panel, BorderLayout.CENTER);
            content.add(status, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }

    private static Set<String> bfs(
            Map<String, List<String>> graph,
            String start,
            String brokenFrom,
            String brokenTo
    ) {
        Queue<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            for (String neighbor : graph.get(current)) {
                boolean broken =
                        (current.equals(brokenFrom) && neighbor.equals(brokenTo))
                                || (current.equals(brokenTo) && neighbor.equals(brokenFrom));

                if (broken) {
                    continue;
                }

                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        return visited;
    }
}
