import 'package:flutter/material.dart';
import 'package:gpa_module/models/subject_model.dart';
import 'package:gpa_module/widgets/subject_card.dart';

class GPACalculatorView extends StatefulWidget {
  final List<SubjectModel> subjects;

  const GPACalculatorView({super.key, required this.subjects});

  @override
  State<GPACalculatorView> createState() => _GPACalculatorViewState();
}

class _GPACalculatorViewState extends State<GPACalculatorView> {
  double gpaResult = 0.0;

  void calculateResult() {
    double totalPoints = 0;
    int totalHours = 0;

    for (var subject in widget.subjects) {
      totalPoints += (subject.creditHours * subject.gradeValue);
      totalHours += subject.creditHours;
    }

    setState(() {
      gpaResult = totalHours == 0 ? 0 : totalPoints / totalHours;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        backgroundColor: Colors.white,

        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: const [
            Text(
              "Calculate GPA",
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            Text(
              "Step 2: Enter grades",
              style: TextStyle(color: Colors.grey, fontSize: 12),
            ),
          ],
        ),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text(
            "Enter credit hours and grades",
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 4),
          const Text(
            "Fill in the details for each subject",
            style: TextStyle(color: Colors.grey),
          ),
          const SizedBox(height: 16),

          ...widget.subjects.map((subject) {
            return SubjectCard(
              subject: subject,
              onUpdate: () => setState(() {}),
            );
          }),

          const SizedBox(height: 24),

          SizedBox(
            width: double.infinity,
            height: 50,
            child: ElevatedButton(
              onPressed: calculateResult,
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.blue,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
              child: const Text(
                "Calculate GPA",
                style: TextStyle(
                  fontSize: 16,
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),

          const SizedBox(height: 24),

          if (gpaResult != 0) ...[
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 32, horizontal: 16),
              decoration: BoxDecoration(
                color: const Color(0xFF4CAF50),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                children: [
                  const Icon(
                    Icons.workspace_premium,
                    size: 60,
                    color: Colors.white,
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    "Your GPA",
                    style: TextStyle(color: Colors.white, fontSize: 16),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    gpaResult.toStringAsFixed(2),
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 48,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    "Based on ${widget.subjects.length} subjects",
                    style: TextStyle(color: Colors.white.withOpacity(0.9)),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 16),

            SizedBox(
              width: double.infinity,
              height: 50,
              child: OutlinedButton(
                onPressed: () {
                  Navigator.of(context).popUntil((route) => route.isFirst);
                },
                style: OutlinedButton.styleFrom(
                  side: const BorderSide(color: Colors.blue),
                ),
                child: const Text("Back to Dashboard"),
              ),
            ),
            const SizedBox(height: 30),
          ],
        ],
      ),
    );
  }
}
