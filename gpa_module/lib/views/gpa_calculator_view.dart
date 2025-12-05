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
  void calculateResult() {
    double totalPoints = 0;
    int totalHours = 0;

    for (var subject in widget.subjects) {
      totalPoints += (subject.creditHours * subject.gradeValue);
      totalHours += subject.creditHours;
    }

    double gpa = totalHours == 0 ? 0 : totalPoints / totalHours;

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Your GPA"),
        backgroundColor: Colors.white,
        content: Text(
          gpa.toStringAsFixed(2),
          style: const TextStyle(
            fontSize: 32,
            fontWeight: FontWeight.bold,
            color: Colors.blue,
          ),
          textAlign: TextAlign.center,
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text("OK"),
          ),
        ],
      ),
    );
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
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 24, 16, 12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: const [
                Text(
                  "Enter credit hours and grades",
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w600,
                    color: Colors.black87,
                  ),
                ),
                SizedBox(height: 4),
                Text(
                  "Fill in the details for each subject",
                  style: TextStyle(fontSize: 14, color: Colors.grey),
                ),
              ],
            ),
          ),

          Expanded(
            child: ListView.builder(
              itemCount: widget.subjects.length,
              itemBuilder: (context, index) {
                return SubjectCard(
                  subject: widget.subjects[index],
                  onUpdate: () {
                    setState(() {});
                  },
                );
              },
            ),
          ),

          Container(
            padding: const EdgeInsets.all(16.0),
            child: SizedBox(
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
          ),
        ],
      ),
    );
  }
}
