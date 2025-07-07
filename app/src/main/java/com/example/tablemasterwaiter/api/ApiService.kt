package com.example.tablemasterwaiter.api

import MenuItem
import com.example.tablemasterwaiter.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("waiters")
    suspend fun getWaiters(): Response<List<Waiter>>

    @GET("tables")
    suspend fun getTables(): Response<List<Table>>

    @POST("/orders")
    suspend fun postOrder(@Body order: Order): Response<Unit>

    @GET("orders")
    suspend fun getOrders(): Response<List<Order>>

    @POST("/login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResult>

    @GET("/tablegroups")
    suspend fun getTableGroups(): Response<List<TableGroup>>
    @GET("/menu")
    suspend fun getMenu(): Response<List<MenuItem>>

    @PUT("orders/{id}")
    suspend fun updateOrder(@Path("id") orderId: Int?, @Body order: Order): Response<Unit>

}
