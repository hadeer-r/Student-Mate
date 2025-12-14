import 'package:flutter/material.dart';
import 'package:gpa_module/models/subject_model.dart';
import 'package:gpa_module/views/gpa_calculator_view.dart';
import 'dart:convert';

// 1. We start the app normally
void main() => runApp(const GPACalaulator());

class GPACalaulator extends StatelessWidget {
  const GPACalaulator({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      initialRoute: '/',
      
      onGenerateRoute: (settings) {
          if (settings.name != null && settings.name!.startsWith('/gpa_calculator')) {
          
          var uri = Uri.parse(settings.name!);
          
          var subjectsJson = uri.queryParameters['subjects'];
          
          List<SubjectModel> finalSubjects = [];
          if (subjectsJson != null) {
            List<dynamic> decodedList = jsonDecode(subjectsJson);
            finalSubjects = decodedList.map((item) {
              return SubjectModel(
                subjectName: item['name'], 
                creditHours: item['credits'], 
                selectedGrade: "A+ (4.0)", // default value
                gradeValue: 4.0,// default value
              );
            }).toList();
          }

          return MaterialPageRoute(
            builder: (context) => GPACalculatorView(subjects: finalSubjects),
          );
        }

        return MaterialPageRoute(builder: (context) => _buildDummyGPAView());
      },
    );
  }

  // for testing
  Widget _buildDummyGPAView() {
    return GPACalculatorView(
      subjects: [
        SubjectModel(
          subjectName: "Test Subject",
          creditHours: 3,
          selectedGrade: "A+ (4.0)",
          gradeValue: 4.0,
        ),
      ],
    );
  }
}