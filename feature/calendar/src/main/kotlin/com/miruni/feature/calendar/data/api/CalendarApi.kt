package com.miruni.feature.calendar.data.api

import com.miruni.core.network.ApiResponse
import com.miruni.feature.calendar.data.dto.request.PatchPlanRequest
import com.miruni.feature.calendar.data.dto.request.PostPlanFinishRequest
import com.miruni.feature.calendar.data.dto.request.PostPlanRequest
import com.miruni.feature.calendar.data.dto.response.BasicPlanResponse
import com.miruni.feature.calendar.data.dto.response.GetDailyPlansResponse
import com.miruni.feature.calendar.data.dto.response.GetMonthlyPlansResponse
import com.miruni.feature.calendar.data.dto.response.GetPlanResponse
import com.miruni.feature.calendar.data.dto.response.PostPlanFinishResponse
import com.miruni.feature.calendar.data.dto.response.GetExpectedDurationResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CalendarApi {

    /** 일정 완료 */
    @POST("api/plans/finish/{planType}/{id}")
    suspend fun postPlanFinish(
        @Path("planType") planType: String,
        @Path("id") planId: Int,
        @Body request: PostPlanFinishRequest
    ): ApiResponse<PostPlanFinishResponse>

    /** 특정 일정 조회 */
    @GET("api/plans/{planId}")
    suspend fun getPlan(
        @Path("planId") planId: Int,
        @Query("planType") planType: String = "BASIC"
    ): ApiResponse<GetPlanResponse>

    /** 일정 예상 소요 시간 조회 */
    @GET("api/plans/{planId}/expected-duration")
    suspend fun getExpectedDuration(
        @Path("planId") planId: Int,
        @Query("planType") planType: String
    ): ApiResponse<GetExpectedDurationResponse>

    /** 캘린더 조회 */
    @GET("api/plans/monthly")
    suspend fun getMonthlyPlans(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): ApiResponse<List<GetMonthlyPlansResponse>>

    /** 특정 날짜의 완료/미완료 일정 조회 */
    @GET("api/plans/daily")
    suspend fun getDailyPlans(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("day") day: Int
    ): ApiResponse<GetDailyPlansResponse>

    /** 일반 일정 생성 */
    @POST("api/plans")
    suspend fun postPlan(
        @Body request: PostPlanRequest
    ): ApiResponse<List<BasicPlanResponse>>

    /** 일반 일정 삭제 */
    @HTTP(
        method = "DELETE",
        path = "api/plans/{basicPlanId}",
        hasBody = true
    )
    suspend fun deletePlan(
        @Path("basicPlanId") basicPlanId: Int
    ): ApiResponse<Int>

    /** 일반 일정 수정 */
    @PATCH("api/plans/{basicPlanId}")
    suspend fun patchPlan(
        @Path("basicPlanId") basicPlanId: Int,
        @Body request: PatchPlanRequest
    ): ApiResponse<BasicPlanResponse>
}