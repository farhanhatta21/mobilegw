package com.example.pblmobile.Api

import com.example.pblmobile.Models.Checkrole
import com.example.pblmobile.Models.LaporResponse
import com.example.pblmobile.Models.UpdateResponse
import com.example.pblmobile.Models.RegisterResponse
import com.example.pblmobile.Models.LoginResponse
import com.example.pblmobile.Models.FullDataResponse
import com.example.pblmobile.Models.UpdateAdmin
import com.example.pblmobile.Models.PointResponse
import com.example.pblmobile.Models.ReducePointsRequest
import com.example.pblmobile.Models.TempatsampahResponse
import com.example.pblmobile.Models.BeritaResponse
import com.example.pblmobile.Models.Beritaku
import com.example.pblmobile.Models.BeritakuResponse
import com.example.pblmobile.Models.DeleteResponse

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {
    @POST("/api/kelompok_1/register.php")
    fun registerUser(@Body requestBody: Map<String, String>): Call<RegisterResponse>

    @POST("/api/kelompok_1/login.php")
    fun loginUser(@Body requestBody: Map<String, String>): Call<LoginResponse>

    @POST("/api/kelompok_1/update.php")
    fun updateUser(@Body requestBody: @JvmSuppressWildcards Map<String, Any>): Call<UpdateResponse>

    @POST("/api/kelompok_1/lapor.php")
    fun reportTrash(@Body requestBody: Map<String, String>): Call<LaporResponse>

    @POST("/api/kelompok_1/melihat_laporan.php")
    fun getLaporan(@Body requestBody: Map<String, Int>): Call<LaporResponse>

    @POST("/api/kelompok_1/fulldata.php")
    fun getFullData(): Call<FullDataResponse>

    @POST("/api/kelompok_1/updatelaporan.php")
    fun updateLaporanStatus(@Body requestBody: Map<String, String>): Call<UpdateAdmin>

    @POST("/api/kelompok_1/laporanselesai.php")
    fun updatePoints(@Body requestBody: Map<String, Int>): Call<UpdateResponse>

    @POST("/api/kelompok_1/melihatpoint.php")
    fun getUserPoints(@Body requestBody: Map<String, Int>): Call<PointResponse>

    @POST("/api/kelompok_1/penguranganpoint.php")
    suspend fun reducePoints(@Body request: ReducePointsRequest): Response<RegisterResponse>

    @POST("/api/kelompok_1/ambildata.php")
    suspend fun getUserData(@Body requestBody: Map<String, Int>): Response<Checkrole>

    @POST("/api/kelompok_1/statslaporan.php")
    fun getLaporanByStatus(@Body requestBody: Map<String, String>): Call<LaporResponse>

    @POST("/api/kelompok_1/laporadmin.php")
    fun laporanadmin(@Body requestBody: Map<String, String>): Call<LaporResponse>

    @GET("/api/kelompok_1/lihat_tempatsampah.php")
    fun getTempatsampahData(): Call<TempatsampahResponse>

    @POST("api/kelompok_1/lihatberita.php")
    fun getBerita(@Body requestBody: Map<String, String>): Call<BeritaResponse>

    @POST("api/kelompok_1/simpanberita.php")
    fun simpanBerita(@Body berita: Beritaku): Call<BeritakuResponse>

    @DELETE("api/kelompok_1/deleteberita.php")
    fun deleteBerita(@Query("id_berita") id_berita: String): Call<DeleteResponse>

    @POST("/api/kelompok_1/deletelapor.php")
    fun deleteLaporan(@Body requestBody: Map<String, String>): Call<DeleteResponse>



}


