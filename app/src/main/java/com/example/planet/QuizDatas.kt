package com.example.planet

enum class QuizType {
    OX, SUBJECTIVE, MATCHING, MULTIPLE_CHOICE
}

data class QuizItem(
    val id: String,
    val chapter: Int,
    val question: String,
    val type: QuizType,
    val correctAnswer: String,
    val hint: String? = null,
    val choices: List<String>? = null,
    val explanation: String? = null
)

// Chapter 1 – OX 퀴즈
val chapter1Quizzes = listOf(
    QuizItem("quiz1_q1", 1, "종이팩은 일반 종이류와 함께 배출한다.", QuizType.OX, "X", explanation = "종이팩은 일반 종이류가 아닌, 전용 수거함에 배출해야 합니다."),
    QuizItem("quiz1_q2", 1, "플라스틱 병은 라벨을 제거하고 배출해야 한다.", QuizType.OX, "O", explanation = "플라스틱 병은 라벨을 제거하고 헹군 후 배출해야 합니다."),
    QuizItem("quiz1_q3", 1, "스티로폼은 재활용이 불가능하다.", QuizType.OX, "X", explanation = "스티로폼도 재활용이 가능하지만, 이물질 제거가 중요합니다."),
    QuizItem("quiz1_q4", 1, "종이컵은 일반 쓰레기로 버린다.", QuizType.OX, "O", explanation = "음료가 묻은 종이컵은 일반 쓰레기로 처리합니다."),
    QuizItem("quiz1_q5", 1, "배터리는 일반 쓰레기로 배출해도 된다.", QuizType.OX, "X", explanation = "배터리는 전용 폐건전지 수거함에 배출해야 합니다.")
)

// Chapter 2 – 주관식 퀴즈
val chapter2Quizzes = listOf(
    QuizItem("quiz2_q1", 2, "바나나 껍질은 ○○○ 쓰레기이다.", QuizType.SUBJECTIVE, "음식물", hint = "ㅇㅅㅁ", explanation = "바나나 껍질은 음식물 쓰레기로 분류됩니다."),
    QuizItem("quiz2_q2", 2, "깨끗한 우유 팩은 ○○○에 배출한다.", QuizType.SUBJECTIVE, "전용 수거함", hint = "ㅈㅇ ㅅㄱㅎ", explanation = "우유 팩은 깨끗이 헹궈 전용 수거함에 배출합니다."),
    QuizItem("quiz2_q3", 2, "고장 난 전자제품은 ○○○ 센터에 가져간다.", QuizType.SUBJECTIVE, "재활용", hint = "ㅈㅎㅇ", explanation = "전자제품은 재활용 센터에 가져가야 합니다."),
    QuizItem("quiz2_q4", 2, "종이와 플라스틱은 ○○○해서 버린다.", QuizType.SUBJECTIVE, "분리", hint = "ㅂㄹ", explanation = "종이와 플라스틱은 각각 분리 배출해야 합니다."),
    QuizItem("quiz2_q5", 2, "음식물이 묻은 종이는 ○○ 쓰레기이다.", QuizType.SUBJECTIVE, "일반", hint = "ㅇㅂ", explanation = "오염된 종이는 일반 쓰레기로 버려야 합니다.")
)

// Chapter 3 – 매칭 퀴즈 (문제-정답 쌍만 정의)
val chapter3Quizzes = listOf(
    QuizItem("quiz3_q1", 3, "깨진 유리컵", QuizType.MATCHING, "신문지 등에 싸서 일반 쓰레기로 배출", explanation = "깨진 유리는 안전을 위해 신문지에 싸서 일반 쓰레기로 처리합니다."),
    QuizItem("quiz3_q2", 3, "종이 영수증", QuizType.MATCHING, "감염지라서 일반 쓰레기에 배출", explanation = "감열지는 재활용이 불가능하므로 일반 쓰레기로 처리합니다."),
    QuizItem("quiz3_q3", 3, "유리병 뚜껑", QuizType.MATCHING, "병과 분리해서 캔류(금속)로 배출", explanation = "유리병 뚜껑은 금속이므로 캔류로 분리 배출합니다."),
    QuizItem("quiz3_q4", 3, "우유 팩", QuizType.MATCHING, "깨끗이 헹궈 종이팩 전용 수거함에 배출", explanation = "우유 팩은 전용 수거함에 헹군 후 배출해야 합니다."),
    QuizItem("quiz3_q5", 3, "배터리", QuizType.MATCHING, "전용 폐건전지 수거함에 배출", explanation = "배터리는 전용 폐건전지 수거함에만 배출해야 합니다.")
)

// Chapter 4 – 객관식 퀴즈
val chapter4Quizzes = listOf(
    QuizItem("quiz4_q1", 4, "다음 중 일반 쓰레기로 버려야 하는 것은?", QuizType.MULTIPLE_CHOICE, "B", choices = listOf("신문지", "음식물이 묻은 종이컵", "깨끗한 플라스틱 컵", "종이 상자"), explanation = "음식물이 묻은 종이컵은 재활용 불가이므로 일반 쓰레기입니다."),
    QuizItem("quiz4_q2", 4, "다음 중 음식물 쓰레기가 아닌 것은?", QuizType.MULTIPLE_CHOICE, "D", choices = listOf("사과껍질", "채소 찌꺼기", "생선 뼈", "일회용 수저"), explanation = "일회용 수저는 음식물이 아니므로 음식물 쓰레기가 아닙니다."),
    QuizItem("quiz4_q3", 4, "전기제품은 어디에 버려야 할까요?", QuizType.MULTIPLE_CHOICE, "C", choices = listOf("일반 쓰레기", "음식물 쓰레기", "재활용 센터", "화장실"), explanation = "전기제품은 재활용 센터를 이용해야 합니다."),
    QuizItem("quiz4_q4", 4, "종이류로 재활용 가능한 것은?", QuizType.MULTIPLE_CHOICE, "A", choices = listOf("신문지", "기름 묻은 종이", "영수증", "벽지"), explanation = "신문지는 재활용이 가능하지만 나머지는 오염 또는 재질 문제로 불가합니다."),
    QuizItem("quiz4_q5", 4, "플라스틱을 배출할 때 바르게 한 행동은?", QuizType.MULTIPLE_CHOICE, "C", choices = listOf("물기 있는 상태로 버림", "라벨 제거 안 함", "깨끗이 씻어서 배출", "음식물과 함께 버림"), explanation = "깨끗이 씻은 후 배출하는 것이 바람직한 처리 방법입니다.")
)

// 전체 통합 리스트
val chapter1FullQuizzes = chapter1Quizzes + chapter2Quizzes + chapter3Quizzes + chapter4Quizzes
