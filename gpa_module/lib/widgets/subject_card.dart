import 'package:flutter/material.dart';
import 'package:gpa_module/models/subject_model.dart';

class SubjectCard extends StatelessWidget {
  final SubjectModel subject;
  final VoidCallback onUpdate;

  const SubjectCard({super.key, required this.subject, required this.onUpdate});

  @override
  Widget build(BuildContext context) {
    return Card(
      color: Colors.white,
      margin: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              subject.subjectName,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w500),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        "Credit Hours",
                        style: TextStyle(color: Colors.grey),
                      ),
                      const SizedBox(height: 5),
                      buildCreditStepper(),
                    ],
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text("Grade", style: TextStyle(color: Colors.grey)),
                      const SizedBox(height: 5),
                      buildGradeDropdown(),
                    ],
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget buildCreditStepper() {
    return Container(
      height: 50,
      padding: const EdgeInsets.symmetric(horizontal: 12),
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        border: Border.all(color: Colors.blueAccent),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            "${subject.creditHours}",
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              InkWell(
                onTap: () {
                  subject.creditHours++;
                  onUpdate();
                },
                child: const Icon(Icons.arrow_drop_up, size: 20),
              ),
              InkWell(
                onTap: () {
                  if (subject.creditHours > 1) {
                    subject.creditHours--;
                    onUpdate();
                  }
                },
                child: const Icon(Icons.arrow_drop_down, size: 20),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget buildGradeDropdown() {
    final Map<String, double> grades = {
      'A+ (4.0)': 4.0,
      'A (3.7)': 3.7,
      'B+ (3.3)': 3.3,
      'B (3.0)': 3.0,
      'C+ (2.7)': 2.7,
      'C (2.4)': 2.4,
      'D+ (2.2)': 2.2,
      'D (2.0)': 2.0,
      'F (0.0)': 0.0,
    };

    return Container(
      height: 50,
      padding: const EdgeInsets.symmetric(horizontal: 12),
      decoration: BoxDecoration(
        color: Colors.grey.shade100,
        border: Border.all(color: Colors.grey.shade300),
        borderRadius: BorderRadius.circular(8),
      ),
      child: DropdownButtonHideUnderline(
        child: DropdownButton<String>(
          dropdownColor: Colors.grey.shade100,
          isExpanded: true,
          value: subject.selectedGrade,
          items: grades.keys.map((String grade) {
            return DropdownMenuItem<String>(value: grade, child: Text(grade));
          }).toList(),
          onChanged: (newValue) {
            if (newValue != null) {
              subject.selectedGrade = newValue;
              subject.gradeValue = grades[newValue]!;
              onUpdate();
            }
          },
        ),
      ),
    );
  }
}
