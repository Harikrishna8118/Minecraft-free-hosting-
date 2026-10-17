package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.ServerDao
import com.example.data.local.ServerEditionConverter
import com.example.data.local.ServerEntity
import com.example.model.MinecraftEdition
import com.example.model.ServerRegion
import com.example.model.ServerSoftware
import com.example.model.ServerStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var serverDao: ServerDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        serverDao = database.serverDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CraftHost", appName)
    }

    @Test
    fun serverDao_insertServer_and_getServerById() = runBlocking {
        val entity = ServerEntity(
            id = "srv-crud-01",
            name = "Survival SMP 1.21",
            motd = "Welcome to CraftHost",
            edition = MinecraftEdition.JAVA,
            software = ServerSoftware.PAPER,
            version = "1.21.4",
            region = ServerRegion.US_EAST,
            address = "smp.crafthost.gg",
            port = 25565,
            status = ServerStatus.ONLINE,
            isAlwaysOnline = true,
            ramGb = 8,
            vCpu = 4,
            storageGb = 60,
            voiceEnabled = true,
            voiceRangeBlocks = 400
        )

        serverDao.insertServer(entity)

        val retrieved = serverDao.getServerById("srv-crud-01")
        assertNotNull("Server must be found in database", retrieved)
        assertEquals("Survival SMP 1.21", retrieved?.name)
        assertEquals(MinecraftEdition.JAVA, retrieved?.edition)
        assertEquals(ServerStatus.ONLINE, retrieved?.status)
        assertEquals(8, retrieved?.ramGb)
        assertEquals(4, retrieved?.vCpu)
        assertEquals(60, retrieved?.storageGb)
        assertTrue(retrieved?.isAlwaysOnline == true)
        assertEquals(400, retrieved?.voiceRangeBlocks)
    }

    @Test
    fun serverDao_updateServer() = runBlocking {
        val entity = ServerEntity(
            id = "srv-crud-02",
            name = "Bedrock Realm",
            edition = MinecraftEdition.BEDROCK,
            status = ServerStatus.OFFLINE,
            ramGb = 4,
            vCpu = 2,
            storageGb = 20
        )
        serverDao.insertServer(entity)

        val retrievedBefore = serverDao.getServerById("srv-crud-02")
        assertEquals(ServerStatus.OFFLINE, retrievedBefore?.status)

        // Update server to ONLINE and upgraded resources
        val updated = entity.copy(
            status = ServerStatus.ONLINE,
            ramGb = 16,
            vCpu = 8,
            storageGb = 100
        )
        serverDao.updateServer(updated)

        val retrievedAfter = serverDao.getServerById("srv-crud-02")
        assertEquals(ServerStatus.ONLINE, retrievedAfter?.status)
        assertEquals(16, retrievedAfter?.ramGb)
        assertEquals(8, retrievedAfter?.vCpu)
        assertEquals(100, retrievedAfter?.storageGb)
    }

    @Test
    fun serverDao_getAllServers_flow() = runBlocking {
        val s1 = ServerEntity(id = "s1", name = "Server Alpha", isAlwaysOnline = true)
        val s2 = ServerEntity(id = "s2", name = "Server Beta", isAlwaysOnline = false)

        serverDao.insertServer(s1)
        serverDao.insertServer(s2)

        val allServers = serverDao.getAllServers().first()
        assertEquals(2, allServers.size)
        assertTrue(allServers.any { it.id == "s1" })
        assertTrue(allServers.any { it.id == "s2" })
    }

    @Test
    fun serverDao_deleteServer() = runBlocking {
        val server = ServerEntity(id = "to-delete", name = "Temp Server")
        serverDao.insertServer(server)

        assertNotNull(serverDao.getServerById("to-delete"))

        serverDao.deleteServer(server)
        assertNull(serverDao.getServerById("to-delete"))
    }

    @Test
    fun serverEditionConverter_convertsCorrectly() {
        val converter = ServerEditionConverter()

        // From edition enum to String
        assertEquals("JAVA", converter.fromEdition(MinecraftEdition.JAVA))
        assertEquals("BEDROCK", converter.fromEdition(MinecraftEdition.BEDROCK))
        assertEquals("JAVA", converter.fromEdition(null))

        // From String to edition enum
        assertEquals(MinecraftEdition.JAVA, converter.toEdition("JAVA"))
        assertEquals(MinecraftEdition.BEDROCK, converter.toEdition("BEDROCK"))
        assertEquals(MinecraftEdition.JAVA, converter.toEdition("UNKNOWN_EDITION"))
        assertEquals(MinecraftEdition.JAVA, converter.toEdition(null))
    }
}
