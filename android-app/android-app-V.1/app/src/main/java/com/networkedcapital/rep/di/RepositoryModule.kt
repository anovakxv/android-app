package com.networkedcapital.rep.di

import com.networkedcapital.rep.data.repository.AuthRepository
import com.networkedcapital.rep.data.repository.AuthRepositoryImpl
import com.networkedcapital.rep.data.repository.GoalRepository
import com.networkedcapital.rep.data.repository.GoalRepositoryImpl
import com.networkedcapital.rep.data.repository.InviteRepository
import com.networkedcapital.rep.data.repository.InviteRepositoryImpl
import com.networkedcapital.rep.data.repository.MessagingRepository
import com.networkedcapital.rep.data.repository.MessagingRepositoryImpl
import com.networkedcapital.rep.data.repository.PaymentRepository
import com.networkedcapital.rep.data.repository.PaymentRepositoryImpl
import com.networkedcapital.rep.data.repository.PortalRepository
import com.networkedcapital.rep.data.repository.PortalRepositoryImpl
import com.networkedcapital.rep.data.repository.UserRepository
import com.networkedcapital.rep.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPortalRepository(
        portalRepositoryImpl: PortalRepositoryImpl
    ): PortalRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    @Singleton
    abstract fun bindMessagingRepository(
        messagingRepositoryImpl: MessagingRepositoryImpl
    ): MessagingRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindInviteRepository(
        inviteRepositoryImpl: InviteRepositoryImpl
    ): InviteRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}
