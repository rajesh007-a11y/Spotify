package com.example.data.local

import com.example.data.model.Song

object CatalogData {
    val initialSongs: List<Song> = listOf(
        // --- HINDI (बॉलीवुड ও হিন্দি গান) ---
        Song(
            id = "song_hi_1",
            title = "Kesariya",
            artist = "Arijit Singh & Pritam",
            album = "Brahmāstra",
            durationMs = 268000L,
            albumArtUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            genre = "Hindi Romantic",
            energy = 0.82f,
            valence = 0.88f,
            bpm = 118,
            language = "Hindi",
            lyrics = """
                [00:00] Mujhko itna bataye koi, kaise tujhse dil na lagaye koi
                [00:20] Rabba ne tujhko banane mein kardi hai husn ki khaali tijoriyan
                [00:44] Kajal ki siyahi se likhi hai tune jaane kitnon ki love storiyan
                [01:05] Kesariya tera ishq hai piya, rang jaaun jo main haath lagaun
                [01:25] Din beete saara teri fikr mein, rain saari teri khair manaun
                [01:45] Patjhad ke mausam mein bhi rangeen bahaar aayi
                [02:05] Tere sang aate hi jaise zindagi muskurayi
            """.trimIndent(),
            isFavorite = true,
            playCount = 48
        ),
        Song(
            id = "song_hi_2",
            title = "Tum Hi Ho",
            artist = "Arijit Singh & Mithoon",
            album = "Aashiqui 2",
            durationMs = 262000L,
            albumArtUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            genre = "Hindi Romantic",
            energy = 0.65f,
            valence = 0.60f,
            bpm = 94,
            language = "Hindi",
            lyrics = """
                [00:00] Hum tere bin ab reh nahi sakte, tere bina kya wajood mera
                [00:22] Tujhse juda gar ho jayenge, toh khud se hi ho jayenge juda
                [00:48] Kyunki tum hi ho, ab tum hi ho, zindagi ab tum hi ho
                [01:14] Chain bhi, mera dard bhi, meri aashiqui ab tum hi ho
                [01:38] Tera mera rishta hai kaisa, ek pal door gawaara nahi
                [02:02] Tere liye har roz hai jeete, tujhko diya mera waqt sabhi
            """.trimIndent(),
            isFavorite = true,
            playCount = 52
        ),
        Song(
            id = "song_hi_3",
            title = "Channa Mereya",
            artist = "Arijit Singh & Pritam",
            album = "Ae Dil Hai Mushkil",
            durationMs = 289000L,
            albumArtUrl = "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            genre = "Hindi Sufi",
            energy = 0.74f,
            valence = 0.52f,
            bpm = 104,
            language = "Hindi",
            lyrics = """
                [00:00] Achha chalta hoon, duaon mein yaad rakhna
                [00:18] Mere zikr ka zubaan pe swaad rakhna
                [00:42] Dil ke sandookon mein mere achhe kaam rakhna
                [01:04] Andhera tera maine le liya, mera ujla sitaara tere naam kiya
                [01:28] Channa mereya mereya, channa mereya mereya beliya
                [01:52] O piya, mehfil mein teri hum na rahein jo, gham toh nahi hai
            """.trimIndent(),
            isFavorite = true,
            playCount = 37
        ),
        Song(
            id = "song_hi_4",
            title = "Raataan Lambiyan",
            artist = "Jubin Nautiyal & Asees Kaur",
            album = "Shershaah",
            durationMs = 230000L,
            albumArtUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            genre = "Hindi Romantic",
            energy = 0.72f,
            valence = 0.80f,
            bpm = 108,
            language = "Hindi",
            lyrics = """
                [00:00] Teri meri gallan ho gayi mashhoor, karna na kabhi mujhe nazron se door
                [00:25] Kithe chaliye tu dhoondte huye, kithe chaliye tu dhoondte huye
                [00:50] Kaatan kaise raataan o saawre, jiya nahi jaata sun baawre
                [01:14] Ke raataan lambiyan lambiyan re, katein tere sangeyan sangeyan re
                [01:38] Cham cham chamke taare saare, tere bina jeena lagda nahi pyare
            """.trimIndent(),
            isFavorite = false,
            playCount = 31
        ),
        Song(
            id = "song_hi_5",
            title = "Ghungroo",
            artist = "Arijit Singh & Shilpa Rao",
            album = "War",
            durationMs = 302000L,
            albumArtUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            genre = "Bollywood Dance",
            energy = 0.94f,
            valence = 0.92f,
            bpm = 126,
            language = "Hindi",
            lyrics = """
                [00:00] Kise poochh kar dhoop aati hai yahan, kise poochh kar aati hai lehar
                [00:22] Chadh rahi hai masti, dhool udi hai subah se dopahar
                [00:44] Aaja aaja mere sang tu chal, hawaon mein nikal
                [01:06] Ghungroo toot gaye, ghungroo toot gaye jab se nache hum
                [01:28] Raat ka safar ho ya din ka aagaaz, har pal hai geet har pal hai saaz
            """.trimIndent(),
            isFavorite = true,
            playCount = 44
        ),

        // --- BENGALI (বাংলা গান ও সুর) ---
        Song(
            id = "song_bn_1",
            title = "Ami Banglay Gaan Gai (আমি বাংলায় গান গাই)",
            artist = "Pratul Mukhopadhyay",
            album = "Banglar Gaan",
            durationMs = 285000L,
            albumArtUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            genre = "Bengali Classic",
            energy = 0.65f,
            valence = 0.90f,
            bpm = 96,
            language = "Bengali",
            lyrics = """
                [00:00] আমি বাংলায় গান গাই, আমি বাংলার গান গাই
                [00:20] আমি আমার আমিকে চিরদিন এই বাংলায় খুঁজে পাই
                [00:45] আমি বাংলায় দেখি স্বপ্ন, আমি বাংলায় বাঁধি সুর
                [01:10] আমি এই বাংলার মায়াভরা পথে হেঁটেছি কত না দূর
                [01:35] বাংলা আমার জীবনানন্দ, বাংলা প্রাণের সুর
                [02:00] বাংলার নদী, বাংলার হাওয়া, আমার মুক্তিপুর
            """.trimIndent(),
            isFavorite = true,
            playCount = 40
        ),
        Song(
            id = "song_bn_2",
            title = "Bhalobashar Morshum (ভালোবাসার মরশুম)",
            artist = "Arijit Singh & Shreya Ghoshal",
            album = "X=Prem",
            durationMs = 238000L,
            albumArtUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            genre = "Bengali Romantic",
            energy = 0.72f,
            valence = 0.86f,
            bpm = 106,
            language = "Bengali",
            lyrics = """
                [00:00] এই মরশুমে তোমায় দিলাম এক চিলতে রোদ
                [00:22] মেঘলা দিনের গল্প গুলো হোক না তবে শোধ
                [00:46] ভালোবাসার মরশুম এলো নতুন করে আজ
                [01:10] তোমার চোখে এঁকে দিলাম সকালের সাজ
                [01:34] হাতটি ধরে হাঁটবো চলো দূর দিগন্তে
                [01:58] বসন্ত এসে ডাক দিল এই বৃষ্টিভেজা প্রবাসে
            """.trimIndent(),
            isFavorite = true,
            playCount = 46
        ),
        Song(
            id = "song_bn_3",
            title = "Mayabono Biharini Horini (মায়াবন বিহারিণী)",
            artist = "Somlata Acharyya Chowdhury",
            album = "Rabindra Fusion Tapes",
            durationMs = 255000L,
            albumArtUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            genre = "Rabindra Sangeet Fusion",
            energy = 0.80f,
            valence = 0.84f,
            bpm = 114,
            language = "Bengali",
            lyrics = """
                [00:00] মায়াবন বিহারিণী হরিণী, গহন স্বপন সঞ্চারিণী
                [00:25] কেন তারে ধরিবারে করি পণ, অকারণ
                [00:50] থাক থাক নিজ মুগ্ধতায়, চঞ্চল চপল গতিভঙ্গিমায়
                [01:15] উড়িয়ে আঁচল দক্ষিণ বাতাসে, আনন্দিত হাসে
                [01:40] তুমি দূর হতে চেয়ে দেখো মায়ার লীলাখেলা
                [02:05] অবুঝ মনের সাধ মিটে যাক এই গোধূলির বেলা
            """.trimIndent(),
            isFavorite = true,
            playCount = 35
        ),
        Song(
            id = "song_bn_4",
            title = "Tumi Ashbe Bole (তুমি আসবে বলে)",
            artist = "Nachiketa Chakraborty",
            album = "Ei Besh Bhalo Achi",
            durationMs = 270000L,
            albumArtUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            genre = "Bengali Modern",
            energy = 0.60f,
            valence = 0.72f,
            bpm = 88,
            language = "Bengali",
            lyrics = """
                [00:00] তুমি আসবে বলেই আকাশ মেঘলা বৃষ্টি এখনো হয়নি
                [00:24] তুমি আসবে বলেই নদীর বুকে নৌকা এখনো বায়নি
                [00:50] তুমি আসবে বলেই শহরটা আজও ট্রাফিকে থমকে দাঁড়ায়
                [01:15] একলা পথিক গলি খুঁজে নেয় সন্ধ্যার ছায়ায়
                [01:40] তুমি আসবে বলেই এই গানের সুর আজও জেগে রয়
                [02:05] নিঃশব্দ রাতও তোমার নামে ভালোবাসার কথা কয়
            """.trimIndent(),
            isFavorite = false,
            playCount = 28
        ),
        Song(
            id = "song_bn_5",
            title = "Bojhena Shey Bojhena (বোঝে না সে বোঝে না)",
            artist = "Arijit Singh & Indraadip Dasgupta",
            album = "Bojhena Shey Bojhena",
            durationMs = 295000L,
            albumArtUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            genre = "Bengali Romantic",
            energy = 0.75f,
            valence = 0.64f,
            bpm = 100,
            language = "Bengali",
            lyrics = """
                [00:00] কিছু কথা ভেসে গেছে কুয়াশায়, কিছু গান রয়ে গেছে অবেলায়
                [00:22] বোঝে না সে বোঝে না, মনের কথা বোঝে না
                [00:46] শুধু চোখে চোখ রেখে দূর থেকে যায় দেখা
                [01:10] হাজার লোকের মাঝে এই মন যে বড়ই একা
                [01:34] কত রাত জেগে লেখা চিঠি ছিঁড়ে ফেলেছি বাতাসে
                [01:58] তবু সেই মুখ ভেসে ওঠে প্রতিটি নিঃশ্বাসে
            """.trimIndent(),
            isFavorite = true,
            playCount = 50
        ),

        // --- GLOBAL & RETRO SYNTH HITS ---
        Song(
            id = "song_1",
            title = "Midnight City Lights",
            artist = "Neon Horizon",
            album = "Electric Odyssey",
            durationMs = 214000L,
            albumArtUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            genre = "Synthwave",
            energy = 0.88f,
            valence = 0.82f,
            bpm = 124,
            lyrics = """
                [00:00] Cruising past neon signs in the midnight rain
                [00:15] Reflections on the dashboard wash away the pain
                [00:32] City pulse beneath the engine, steady and alive
                [00:48] We got endless asphalt, nowhere to arrive
                [01:04] Neon lights, guide us home
            """.trimIndent(),
            isFavorite = true,
            playCount = 18
        ),
        Song(
            id = "song_2",
            title = "Starlight Serenade",
            artist = "Aura Bloom",
            album = "Pastel Sky",
            durationMs = 195000L,
            albumArtUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            genre = "Indie Pop",
            energy = 0.68f,
            valence = 0.90f,
            bpm = 112,
            lyrics = """
                [00:00] Morning sun spilling through the balcony blinds
                [00:18] Leaving yesterday's heavy thoughts behind
                [00:36] Just your laughter echoing in the kitchen hall
                [00:54] We don't need fortune, we already have it all
            """.trimIndent(),
            isFavorite = true,
            playCount = 12
        ),
        Song(
            id = "song_3",
            title = "Coffee & Raindrops",
            artist = "Lofi Study Collective",
            album = "Sunday Afternoon Chill",
            durationMs = 168000L,
            albumArtUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            genre = "Lo-Fi / Chillhop",
            energy = 0.35f,
            valence = 0.58f,
            bpm = 84,
            lyrics = """
                [00:00] (Vinyl crackle and warm Rhodes chords)
                [00:20] Rain tapping soft on glass panes
                [00:45] Steaming ceramic cup, warmth through my hands
            """.trimIndent(),
            isFavorite = false,
            playCount = 24
        ),
        Song(
            id = "song_4",
            title = "Cybernetic Pulse",
            artist = "Vektor 99",
            album = "Subterranean Network",
            durationMs = 242000L,
            albumArtUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            genre = "Electronic / Techno",
            energy = 0.95f,
            valence = 0.70f,
            bpm = 132,
            lyrics = """
                [00:00] Binary stream locked into sync
                [00:25] Bassline shaking the warehouse concrete
                [00:50] Strobes cutting through the fog
            """.trimIndent(),
            isFavorite = true,
            playCount = 15
        ),
        Song(
            id = "song_8",
            title = "Hyperdrive 1986",
            artist = "Turbo Synth",
            album = "Outrun Sunset",
            durationMs = 198000L,
            albumArtUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=600&auto=format&fit=crop&q=80",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            genre = "Synthwave",
            energy = 0.94f,
            valence = 0.88f,
            bpm = 130,
            lyrics = """
                [00:00] Key in the ignition, red dials glowing bright
                [00:18] Gearing up for the midnight flight
                [00:38] Laser grid stretching to infinity
            """.trimIndent(),
            isFavorite = true,
            playCount = 29
        )
    )

    val initialPlaylists: List<PlaylistEntity> = listOf(
        PlaylistEntity(
            id = "pl_bollywood_hits",
            title = "Bollywood Melodies & Romance",
            description = "Soulful Hindi chartbusters featuring Arijit Singh, Pritam, and Jubin Nautiyal.",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600&auto=format&fit=crop&q=80",
            songIdsJson = "song_hi_1,song_hi_2,song_hi_3,song_hi_4,song_hi_5",
            isAiGenerated = false
        ),
        PlaylistEntity(
            id = "pl_bangla_classics",
            title = "Bangla Gaan: Arijit & Rabindra Fusion (বাংলা)",
            description = "The timeless magic of Bengal: Nachiketa, Pratul Mukhopadhyay, and Arijit's best Bengali melodies.",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80",
            songIdsJson = "song_bn_1,song_bn_2,song_bn_3,song_bn_4,song_bn_5",
            isAiGenerated = false
        ),
        PlaylistEntity(
            id = "pl_ai_dj",
            title = "DJ Soundify • Hindi & Bengali Flow",
            description = "AI curated set tailored to your soulful Hindi ballads, Bengali melodies, and upbeat grooves.",
            coverUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=600&auto=format&fit=crop&q=80",
            songIdsJson = "song_hi_1,song_bn_2,song_hi_2,song_bn_5,song_hi_5",
            isAiGenerated = true,
            curatorNotes = "DJ Soundify: 'Arijit Singh vibes with classic Kolkata romance and driving rhythms. Hand-picked for you!'"
        ),
        PlaylistEntity(
            id = "pl_synth_heavy",
            title = "Neon Nights & Retrowave",
            description = "Pulsing analog synthesizers, midnight drives, and 80s nostalgia.",
            coverUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=600&auto=format&fit=crop&q=80",
            songIdsJson = "song_1,song_8,song_4",
            isAiGenerated = false
        ),
        PlaylistEntity(
            id = "pl_chill_study",
            title = "Deep Focus & Lo-Fi Beats",
            description = "Gentle beats to study, code, and relax to. Zero interruptions.",
            coverUrl = "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=80",
            songIdsJson = "song_3,song_bn_4,song_2",
            isAiGenerated = false
        )
    )
}
