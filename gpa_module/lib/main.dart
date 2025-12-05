import 'package:flutter/material.dart';
import 'package:gpa_module/models/subject_model.dart';
import 'package:gpa_module/views/gpa_calculator_view.dart';

void main() => runApp(GPACalaulator());

class GPACalaulator extends StatelessWidget {
  const GPACalaulator({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: GPACalculatorView(
        subjects: [
          SubjectModel(
            subjectName: "Data Structures",
            creditHours: 3,
            selectedGrade: "B (3.0)",
            gradeValue: 3.0,
          ),
          SubjectModel(
            subjectName: "Web Development",
            creditHours: 2,
            selectedGrade: "A (3.7)",
            gradeValue: 3.7,
          ),
          SubjectModel(
            subjectName: "Database Systems",
            creditHours: 3,
            selectedGrade: "B+ (3.3)",
            gradeValue: 3.3,
          ),
        ],
      ),
    );
  }
}
