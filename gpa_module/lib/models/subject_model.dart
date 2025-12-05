class SubjectModel {
  final String subjectName;
  final int creditHours;
  final double gradePoint;

  SubjectModel({
    required this.subjectName,
    required this.creditHours,
    required this.gradePoint,
  });

  factory SubjectModel.fromJson(Map<String, dynamic> json) {
    return SubjectModel(
      subjectName: json['name'],
      creditHours: json['credits'],
      gradePoint: json['grade'].toDouble(),
    );
  }
}
