package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CraftHostDatabase
import com.example.data.local.MinecraftServerDao
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

    private lateinit var database: CraftHostDatabase
    private lateinit var serverDao: MinecraftServerDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CraftHostDatabase::class.java)
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
    fun room_insertAndRetrieveServerEntity() = runBlocking {
        val entity = ServerEntity(
            id = "test-server-01",
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

        val retrieved = serverDao.getServerById("test-server-01")
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
    fun room_updateServerStatusAndResources() = runBlocking {
        val entity = ServerEntity(
            id = "test-server-02",
            name = "Bedrock Realm",
            edition = MinecraftEdition.BEDROCK,
            status = ServerStatus.ONLINE,
            ramGb = 4,
            vCpu = 2,
            storageGb = 20
        )
        serverDao.insertServer(entity)

        // Update status to RECOVERING
        serverDao.updateServerStatus("test-server-02", ServerStatus.RECOVERING)
        val updatedStatus = serverDao.getServerById("test-server-02")
        assertEquals(ServerStatus.RECOVERING, updatedStatus?.status)

        // Reallocate resources
        serverDao.updateResourceConfig("test-server-02", ramGb = 12, vCpu = 6, storageGb = 80)
        val updatedResources = serverDao.getServerById("test-server-02")
        assertEquals(12, updatedResources?.ramGb)
        assertEquals(6, updatedResources?.vCpu)
        assertEquals(80, updatedResources?.storageGb)
    }

    @Test
    fun room_queryAlwaysOnlineServersFlow() = runBlocking {
        val s1 = ServerEntity(id = "s1", name = "Always Online Server", isAlwaysOnline = true)
        val s2 = ServerEntity(id = "s2", name = "On Demand Server", isAlwaysOnline = false)

        serverDao.insertServers(listOf(s1, s2))

        val alwaysOnlineList = serverDao.getAlwaysOnlineServers().first()
        assertEquals(1, alwaysOnlineList.size)
        assertEquals("s1", alwaysOnlineList.first().id)
    }

    @Test
    fun room_deleteServerById() = runBlocking {
        val server = ServerEntity(id = "to-delete", name = "Temp Server")
        serverDao.insertServer(server)

        assertNotNull(serverDao.getServerById("to-delete"))

        serverDao.deleteServerById("to-delete")
        assertNull(serverDao.getServerById("to-delete"))
    }
}
