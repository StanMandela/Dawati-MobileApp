package com.ke.dawaati.api

import com.ke.dawaati.api.response.ActivateResponse
import com.ke.dawaati.api.response.EBooksResponse
import com.ke.dawaati.api.response.EvaluationAllAttemptsResponse
import com.ke.dawaati.api.response.EvaluationAttemptDetailsResponse
import com.ke.dawaati.api.response.EvaluationQuestionsResponse
import com.ke.dawaati.api.response.EvaluationResponse
import com.ke.dawaati.api.response.IndexResponse
import com.ke.dawaati.api.response.LoginResponse
import com.ke.dawaati.api.response.PracticalsResponse
import com.ke.dawaati.api.response.RegisterResponse
import com.ke.dawaati.api.response.ResetResponse
import com.ke.dawaati.api.response.SchoolResponse
import com.ke.dawaati.api.response.SubmitEvaluationResponse
import com.ke.dawaati.api.response.SubscribeResponse
import com.ke.dawaati.api.response.VideosResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface DawatiAPI {
    @POST("schools")
    suspend fun fetchSchools(): SchoolResponse

    @POST("login")
    @FormUrlEncoded
    suspend fun loginUser(
        @Field("email_phone") email_phone: String,
        @Field("password") password: String,
        @Field("user_token") user_token: String
    ): LoginResponse

    @POST("signup")
    @FormUrlEncoded
    suspend fun registerUser(
        @Field("full_name") full_name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("gender") gender: String,
        @Field("level") level: String,
        @Field("school_name") school_name: String,
        @Field("password") password: String,
        @Field("confirmPassword") confirmPassword: String,
        @Field("user_token") user_token: String
    ): RegisterResponse

    @POST("confirm_user")
    @FormUrlEncoded
    suspend fun activateUser(
        @Field("email") email: String,
        @Field("code") code: String
    ): ActivateResponse

    @POST("reset_password")
    @FormUrlEncoded
    suspend fun resetPassword(
        @Field("email") email: String
    ): ResetResponse

    @POST("stk_push")
    @FormUrlEncoded
    suspend fun subscribePremium(
        @Field("mobile") mobile: String,
        @Field("subscription_type") subscription_type: String,
        @Field("user_id") user_id: String
    ): SubscribeResponse

    @GET("index")
    suspend fun fetchSubjects(): IndexResponse

    @POST("topics")
    @FormUrlEncoded
    suspend fun fetchClassVideos(
        @Field("level") level: String = "level_001",
        @Field("subject") subject: String = "Mathematics"
    ): VideosResponse

    @POST("practicals")
    @FormUrlEncoded
    suspend fun fetchLabPracticalVideos(
        @Field("level") level: String = "level_001",
        @Field("subject") subject: String = "Mathematics"
    ): PracticalsResponse

    @POST("revision_videos")
    @FormUrlEncoded
    suspend fun fetchExamVideos(
        @Field("level") level: String = "level_001",
        @Field("subject") subject: String = "Mathematics"
    ): VideosResponse

    @POST("books")
    @FormUrlEncoded
    suspend fun fetchClassBooks(
        @Field("level") level: String = "level_001",
        @Field("subject") subject: String = "Mathematics"
    ): EBooksResponse

    @POST("revision_books")
    @FormUrlEncoded
    suspend fun fetchRevisionBooks(
        @Field("level") level: String = "level_001",
        @Field("subject") subject: String = "Mathematics"
    ): EBooksResponse

    @POST("evaluations/exams")
    @FormUrlEncoded
    suspend fun fetchAllSubjectExams(
        @Field("subject") subject: String = "6"
    ): EvaluationResponse

    @POST("evaluations/exam")
    @FormUrlEncoded
    suspend fun fetchSingleExam(
        @Field("action") action: String = "attempt",
        @Field("exam_id") exam_id: String = "Mathematics"
    ): EvaluationQuestionsResponse

    @POST("evaluations/all_attempts")
    @FormUrlEncoded
    suspend fun fetchEvaluationAttempts(
        @Field("user_id") user_id: String = "level_001"
    ): EvaluationAllAttemptsResponse

    @POST("evaluations/reports")
    @FormUrlEncoded
    suspend fun fetchEvaluationReport(
        @Field("user_id") user_id: String = "level_001",
        @Field("exam_id") exam_id: String = "Mathematics",
        @Field("attempt_id") attempt_id: String = "Mathematics"
    ): EvaluationAttemptDetailsResponse

    // @Field("response") response: List<ExamAnswers> = emptyList()
    @POST("evaluations/submit")
    @FormUrlEncoded
    suspend fun submitEvaluation(
        @Field("user_id") user_id: String = "",
        @Field("exam_id") exam_id: String = "",
        @Field("response") response: String = ""
    ): SubmitEvaluationResponse

    @PUT("analytics/user_analytics")
    @FormUrlEncoded
    suspend fun submitAnalytics(
        @Field("userID") user_id: String = "",
        @Field("analytics") analytics: String = ""
    ): SubmitEvaluationResponse
}
