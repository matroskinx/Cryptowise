package com.kvladislav.cryptowise.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kvladislav.cryptowise.database.dao.PortfolioDao
import com.kvladislav.cryptowise.database.dao.TransactionDao
import com.kvladislav.cryptowise.models.portfolio.PortfolioItem
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction

@Database(
    entities = [BuySellTransaction::class, PortfolioItem::class],
    version = 1,
    exportSchema = false
)
public abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    abstract fun portfolioDao(): PortfolioDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        fun getDatabase(context: Context): TransactionDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}