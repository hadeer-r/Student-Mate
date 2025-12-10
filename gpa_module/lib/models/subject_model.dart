class SubjectModel {
  final String subjectName;
  int creditHours;
  String selectedGrade;
  double gradeValue;

  SubjectModel({
    required this.selectedGrade,
    required this.gradeValue,
    required this.subjectName,
    required this.creditHours,
  });

  factory SubjectModel.fromJson(Map<String, dynamic> json) {
    return SubjectModel(
      subjectName: json['name'],
      creditHours: json['credits'],
      selectedGrade: json['selectedGrade'] ?? 'B',
      gradeValue: json['gradeValue']?.toDouble() ?? 3.0,
    );
  }
}
