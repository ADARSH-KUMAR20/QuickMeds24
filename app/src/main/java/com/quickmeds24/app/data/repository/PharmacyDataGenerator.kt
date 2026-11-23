package com.quickmeds24.app.data.repository

import com.quickmeds24.app.data.model.*
import java.util.Locale
import kotlin.random.Random

object PharmacyDataGenerator {

    private val ALPHA_1_COORDS = Coordinates(28.4735, 77.5098)
    private val ALPHA_2_COORDS = Coordinates(28.4770, 77.5140)

    val LOCATIONS = listOf(
        DeliveryLocation(
            id = "loc-alpha-1",
            area = "Alpha 1",
            landmark = "Commercial Belt & Metro Station",
            city = "Greater Noida",
            state = "Uttar Pradesh",
            pincode = "201310",
            fullAddress = "Flat 402, Tower B, Golf Vista, Alpha 1, Greater Noida - 201310",
            coordinates = ALPHA_1_COORDS
        ),
        DeliveryLocation(
            id = "loc-alpha-2",
            area = "Alpha 2",
            landmark = "Main Sector Market & Pocket F",
            city = "Greater Noida",
            state = "Uttar Pradesh",
            pincode = "201310",
            fullAddress = "House 128, Pocket F, Alpha 2, Greater Noida - 201310",
            coordinates = ALPHA_2_COORDS
        ),
        DeliveryLocation(
            id = "loc-beta-1",
            area = "Beta 1",
            landmark = "Near Beta Plaza",
            city = "Greater Noida",
            state = "Uttar Pradesh",
            pincode = "201308",
            fullAddress = "Villa 45, Sector Beta 1, Greater Noida - 201308",
            coordinates = Coordinates(28.4820, 77.5020)
        ),
        DeliveryLocation(
            id = "loc-gamma-2",
            area = "Gamma 2",
            landmark = "Near Ryan International School",
            city = "Greater Noida",
            state = "Uttar Pradesh",
            pincode = "201308",
            fullAddress = "Pocket C, Gamma 2, Greater Noida - 201308",
            coordinates = Coordinates(28.4880, 77.5180)
        ),
        DeliveryLocation(
            id = "loc-pari-chowk",
            area = "Pari Chowk",
            landmark = "Near Metro Station & Ansal Plaza",
            city = "Greater Noida",
            state = "Uttar Pradesh",
            pincode = "201310",
            fullAddress = "Near Ansal Plaza Mall, Pari Chowk, Greater Noida - 201310",
            coordinates = Coordinates(28.4650, 77.5130)
        ),
        DeliveryLocation(
            id = "loc-kp-2",
            area = "Knowledge Park 2",
            landmark = "Near Metro Station & College Hub",
            city = "Greater Noida",
            state = "Uttar Pradesh",
            pincode = "201310",
            fullAddress = "Campus Commercial Belt, Knowledge Park 2, Greater Noida - 201310",
            coordinates = Coordinates(28.4650, 77.4950)
        ),
        DeliveryLocation(
            id = "loc-sec-22",
            area = "Sector 22",
            landmark = "Near Sector 22 Central Market",
            city = "Noida / Greater Noida",
            state = "Uttar Pradesh",
            pincode = "201301",
            fullAddress = "Main Road, Sector 22 Market, Uttar Pradesh - 201301",
            coordinates = Coordinates(28.5200, 77.4700)
        ),
        DeliveryLocation(
            id = "loc-sec-76",
            area = "Sector 76",
            landmark = "Near Sector 76 Metro Station & Silicon City",
            city = "Noida",
            state = "Uttar Pradesh",
            pincode = "201301",
            fullAddress = "Amrapali Silicon Arcade, Sector 76, Noida - 201301",
            coordinates = Coordinates(28.5600, 77.3900)
        )
    )

    // Base template of 50+ master medicines
    private val MASTER_MEDICINE_TEMPLATES = listOf(
        Triple("Telma 40 Tablet", "Telmisartan 40mg", "Glenmark"),
        Triple("Telma 80 Tablet", "Telmisartan 80mg", "Glenmark"),
        Triple("Telma H Tablet", "Telmisartan 40mg + Hydrochlorothiazide 12.5mg", "Glenmark"),
        Triple("Telma AM Tablet", "Telmisartan 40mg + Amlodipine 5mg", "Glenmark"),
        Triple("Telma CT 40/12.5 Tablet", "Telmisartan 40mg + Chlorthalidone 12.5mg", "Glenmark"),
        Triple("Dolo 650 Tablet", "Paracetamol 650mg", "Micro Labs"),
        Triple("Calpol 500 Tablet", "Paracetamol 500mg", "GSK"),
        Triple("Calpol 650 Tablet", "Paracetamol 650mg", "GSK"),
        Triple("Crocin Advance Tablet", "Paracetamol 500mg Fast Action", "Haleon"),
        Triple("Crocin Pain Relief Tablet", "Paracetamol 650mg + Caffeine 50mg", "Haleon"),
        Triple("Combiflam Tablet", "Ibuprofen 400mg + Paracetamol 325mg", "Sanofi"),
        Triple("Meftal Spas Tablet", "Mefenamic Acid 250mg + Dicyclomine 10mg", "Blue Cross"),
        Triple("Meftal Forte Tablet", "Mefenamic Acid 500mg + Paracetamol 325mg", "Blue Cross"),
        Triple("Zerodol-P Tablet", "Aceclofenac 100mg + Paracetamol 325mg", "Ipca"),
        Triple("Zerodol-SP Tablet", "Aceclofenac 100mg + Paracetamol 325mg + Serratiopeptidase 15mg", "Ipca"),
        Triple("Glycomet-GP 1 Tablet", "Glimepiride 1mg + Metformin 500mg", "USV"),
        Triple("Glycomet-GP 2 Tablet", "Glimepiride 2mg + Metformin 500mg", "USV"),
        Triple("Glycomet 500 SR Tablet", "Metformin 500mg Sustained Release", "USV"),
        Triple("Glycomet 850 Tablet", "Metformin 850mg", "USV"),
        Triple("Januvia 100mg Tablet", "Sitagliptin 100mg", "MSD"),
        Triple("Janumet 50/500 Tablet", "Sitagliptin 50mg + Metformin 500mg", "MSD"),
        Triple("Galvus Met 50/500 Tablet", "Vildagliptin 50mg + Metformin 500mg", "Novartis"),
        Triple("Forxiga 10mg Tablet", "Dapagliflozin 10mg", "AstraZeneca"),
        Triple("Augmentin 625 Duo Tablet", "Amoxicillin 500mg + Clavulanic Acid 125mg", "GSK"),
        Triple("Moxikind CV 625 Tablet", "Amoxicillin 500mg + Clavulanic Acid 125mg", "Mankind"),
        Triple("Clavam 625 Tablet", "Amoxicillin 500mg + Clavulanic Acid 125mg", "Alkem"),
        Triple("Azithral 500 Tablet", "Azithromycin 500mg", "Alembic"),
        Triple("Azee 500 Tablet", "Azithromycin 500mg", "Cipla"),
        Triple("Zifi 200 Tablet", "Cefixime 200mg", "FDC"),
        Triple("Taxim-O 200 Tablet", "Cefixime 200mg", "Alkem"),
        Triple("Ciplox 500 Tablet", "Ciprofloxacin 500mg", "Cipla"),
        Triple("Pan 40 Tablet", "Pantoprazole 40mg", "Alkem"),
        Triple("Pantocid 40 Tablet", "Pantoprazole 40mg", "Sun Pharma"),
        Triple("Pan D Capsule", "Pantoprazole 40mg + Domperidone 30mg SR", "Alkem"),
        Triple("Pantop DSR Capsule", "Pantoprazole 40mg + Domperidone 30mg SR", "Aristo"),
        Triple("Razo D Capsule", "Rabeprazole 20mg + Domperidone 30mg", "Dr. Reddy's"),
        Triple("Omez 20 Capsule", "Omeprazole 20mg", "Dr. Reddy's"),
        Triple("Montair LC Tablet", "Montelukast 10mg + Levocetirizine 5mg", "Cipla"),
        Triple("Telekast L Tablet", "Montelukast 10mg + Levocetirizine 5mg", "Lupin"),
        Triple("Allegra 120mg Tablet", "Fexofenadine 120mg", "Sanofi"),
        Triple("Allegra 180mg Tablet", "Fexofenadine 180mg", "Sanofi"),
        Triple("Cetzine 10mg Tablet", "Cetirizine 10mg", "GSK"),
        Triple("Okacet Tablet", "Cetirizine 10mg", "Cipla"),
        Triple("Ascoril D Plus Syrup", "Dextromethorphan + Chlorpheniramine + Phenylephrine", "Glenmark"),
        Triple("Alex Cough Syrup", "Dextromethorphan + Chlorpheniramine", "Glenmark"),
        Triple("Benadryl Cough Syrup", "Diphenhydramine + Ammonium Chloride", "Johnson & Johnson"),
        Triple("Sinarest New Tablet", "Paracetamol + Phenylephrine + Chlorpheniramine", "Centaur"),
        Triple("Cheston Cold Tablet", "Cetirizine + Paracetamol + Phenylephrine", "Cipla"),
        Triple("Becosules Z Capsule", "Vitamin B-Complex + Vitamin C + Zinc", "Pfizer"),
        Triple("Neurobion Forte Tablet", "Vitamin B1, B6, B12 Complex", "P&G Health"),
        Triple("Shelcal 500 Tablet", "Calcium 500mg + Vitamin D3 250 IU", "Torrent"),
        Triple("Shelcal HD Tablet", "Calcium 500mg + Vitamin D3 500 IU", "Torrent"),
        Triple("Limcee 500mg Chewable", "Vitamin C (Ascorbic Acid) 500mg", "Abbott"),
        Triple("Zincovit Tablet", "Multivitamin + Multimineral with Grape Seed Extract", "Apex"),
        Triple("Evion 400 Capsule", "Vitamin E 400mg", "P&G Health"),
        Triple("D-Rise 60K Capsule", "Cholecalciferol (Vitamin D3) 60,000 IU", "USV"),
        Triple("Calcirol Sachet", "Cholecalciferol Granules 60,000 IU", "Cadila"),
        Triple("Betadine 10% Ointment 20g", "Povidone Iodine 10% w/w", "Win-Medicare"),
        Triple("Soframycin Skin Cream 30g", "Framycetin Sulphate 1% w/w", "Sanofi"),
        Triple("Volini Pain Relief Gel 30g", "Diclofenac + Linseed Oil + Methyl Salicylate", "Sun Pharma"),
        Triple("Moov Pain Relief Spray 50g", "Ayurvedic Pain Relief Formula", "Reckitt"),
        Triple("Omnigel 30g", "Diclofenac Diethylamine + Virgin Linseed Oil", "Cipla"),
        Triple("Candid Dusting Powder 100g", "Clotrimazole 1% w/w", "Glenmark"),
        Triple("Ciplox Eye/Ear Drops 10ml", "Ciprofloxacin 0.3% w/v", "Cipla"),
        Triple("Refresh Tears Eye Drops 10ml", "Carboxymethylcellulose Sodium 0.5%", "Allergan"),
        Triple("Otrivin Oxy Fast Relief 10ml", "Oxymetazoline HCl 0.05%", "GSK"),
        Triple("Accu-Chek Active 50 Strips", "Blood Glucose Test Strips", "Roche"),
        Triple("OneTouch Select Plus 50 Strips", "Blood Glucose Test Strips", "LifeScan"),
        Triple("Dettol Antiseptic Liquid 250ml", "Chloroxylenol Antiseptic", "Reckitt"),
        Triple("Savlon Antiseptic Liquid 200ml", "Chlorhexidine Gluconate + Cetrimide", "ITC"),
        Triple("Cotton Roll 100g", "Absorbent Surgical Cotton", "Dettol Medical"),
        Triple("Crepe Bandage 10cm", "Elastic Orthopedic Cotton Bandage", "Flamingo"),
        Triple("Digital Thermometer", "Waterproof Fast Reading Thermometer", "Dr. Morepen")
    )

    private val DOSAGE_FORMS = listOf(
        "Strip of 10 Tablets", "Strip of 15 Tablets", "Strip of 10 Capsules",
        "Bottle of 100ml Syrup", "Bottle of 200ml Syrup", "Tube of 30g Gel",
        "Tube of 20g Ointment", "Pack of 50 Strips", "Bottle of 10ml Drops"
    )

    /**
     * Generates 500+ medicines with realistic pricing and details.
     */
    fun generateMedicinesForPharmacy(pharmacyId: String, pharmacyName: String, sector: String): List<Medicine> {
        val medicines = mutableListOf<Medicine>()
        val rnd = Random(pharmacyId.hashCode())

        // 1. Add core templates
        var index = 1
        MASTER_MEDICINE_TEMPLATES.forEach { (name, generic, brand) ->
            val basePrice = when {
                name.contains("Strips") -> 850.0 + rnd.nextInt(150)
                name.contains("Thermometer") -> 220.0 + rnd.nextInt(80)
                name.contains("Syrup") -> 95.0 + rnd.nextInt(55)
                name.contains("Ointment") || name.contains("Gel") -> 110.0 + rnd.nextInt(60)
                name.contains("Telma") -> 210.0 + rnd.nextInt(80)
                name.contains("Glycomet") -> 180.0 + rnd.nextInt(90)
                name.contains("Augmentin") || name.contains("Clavam") -> 190.0 + rnd.nextInt(40)
                name.contains("60K") -> 140.0 + rnd.nextInt(40)
                else -> 40.0 + rnd.nextInt(160)
            }
            val mrp = (basePrice * 1.15).coerceAtLeast(basePrice + 5.0)
            val discount = (((mrp - basePrice) / mrp) * 100).toInt()

            medicines.add(
                Medicine(
                    id = "med-${pharmacyId}-$index",
                    name = name,
                    genericName = generic,
                    brand = brand,
                    category = categorizeMedicine(name),
                    dosageForm = when {
                        name.contains("Syrup") -> "Bottle of 100ml Syrup"
                        name.contains("Gel") || name.contains("Ointment") -> "Tube of 30g"
                        name.contains("Capsule") -> "Strip of 10 Capsules"
                        name.contains("Strips") -> "Pack of 50 Test Strips"
                        name.contains("Drops") -> "Bottle of 10ml Drops"
                        else -> "Strip of 10 Tablets"
                    },
                    price = Math.round(basePrice * 100.0) / 100.0,
                    mrp = Math.round(mrp * 100.0) / 100.0,
                    discountPercentage = discount,
                    inStock = rnd.nextInt(100) > 5, // 95% in stock
                    stockCount = 10 + rnd.nextInt(90),
                    prescriptionRequired = isPrescriptionNeeded(name),
                    description = "Manufactured by $brand. Genuine certified batch for $sector. Used for treatment under medical supervision.",
                    composition = generic,
                    manufacturer = brand,
                    imageUrl = getMedicineImageUrl(name),
                    tags = listOf(brand.lowercase(), sector.lowercase(), generic.lowercase(), name.split(" ").first().lowercase())
                )
            )
            index++
        }

        // 2. Expand to reach 500+ medicines with realistic strengths, combinations & pack sizes
        val strengths = listOf("5mg", "10mg", "20mg", "25mg", "40mg", "50mg", "75mg", "100mg", "200mg", "250mg", "500mg", "650mg", "1g", "Forte", "Plus", "SR", "Duo", "Max")
        val forms = listOf("Tablet", "Capsule", "Drops", "Syrup", "Suspension", "Mouthwash", "Sachet", "Effervescent", "Injection")

        val prefixList = listOf(
            "Paracetamol", "Azithromycin", "Amoxicillin", "Cefixime", "Ofloxacin", "Metronidazole",
            "Ciprofloxacin", "Pantoprazole", "Rabeprazole", "Omeprazole", "Domperidone", "Ondansetron",
            "Levocetirizine", "Cetirizine", "Montelukast", "Fexofenadine", "Aceclofenac", "Diclofenac",
            "Ibuprofen", "Tramadol", "Pregabalin", "Gabapentin", "Methylcobalamin", "Calcium Citrate",
            "Vitamin D3", "Vitamin C", "Zinc Sulphate", "Iron Folic Acid", "Lactobacillus", "Telmisartan",
            "Amlodipine", "Metoprolol", "Atenolol", "Losartan", "Atorvastatin", "Rosuvastatin",
            "Metformin", "Glimepiride", "Vildagliptin", "Teneligliptin", "Dapagliflozin", "Empagliflozin",
            "Sitagliptin", "Voglibose", "Pioglitazone", "Spironolactone", "Torsemide", "Hydrochlorothiazide"
        )

        val pharmaBrands = listOf("Sun Pharma", "Cipla", "Dr. Reddy's", "Lupin", "Torrent", "Zydus", "Alkem", "Glenmark", "Mankind", "Abbott", "Pfizer", "Sanofi", "Micro Labs", "USV", "Ipca")

        while (medicines.size < 510) {
            val prefix = prefixList[rnd.nextInt(prefixList.size)]
            val strength = strengths[rnd.nextInt(strengths.size)]
            val form = forms[rnd.nextInt(forms.size)]
            val brand = pharmaBrands[rnd.nextInt(pharmaBrands.size)]
            val medName = "$prefix $strength $form"
            val price = 30.0 + rnd.nextInt(320)
            val mrp = price + 15.0 + rnd.nextInt(30)

            medicines.add(
                Medicine(
                    id = "med-${pharmacyId}-$index",
                    name = medName,
                    genericName = "$prefix $strength",
                    brand = brand,
                    category = categorizeMedicine(prefix),
                    dosageForm = DOSAGE_FORMS[rnd.nextInt(DOSAGE_FORMS.size)],
                    price = Math.round(price * 100.0) / 100.0,
                    mrp = Math.round(mrp * 100.0) / 100.0,
                    discountPercentage = 10 + rnd.nextInt(20),
                    inStock = rnd.nextInt(100) > 8,
                    stockCount = 5 + rnd.nextInt(50),
                    prescriptionRequired = isPrescriptionNeeded(prefix),
                    description = "Pharmaceutical grade $medName by $brand. Verified batch at $pharmacyName.",
                    composition = "$prefix $strength IP",
                    manufacturer = brand,
                    imageUrl = getMedicineImageUrl(prefix),
                    tags = listOf(prefix.lowercase(), brand.lowercase(), sector.lowercase(), "medicine")
                )
            )
            index++
        }

        return medicines
    }

    /**
     * Builds all 50+ pharmacies with exact Alpha 1 (3 pharmacies), Alpha 2 (2 pharmacies),
     * and Greater Noida sectors.
     */
    fun generateAllPharmacies(): List<Pharmacy> {
        val list = mutableListOf<Pharmacy>()

        // 1. Alpha 1 Pharmacies (Exact 3 Pharmacies)
        list.add(
            Pharmacy(
                id = "pharm-alpha1-sharma",
                name = "Sharma Medical Store",
                branch = "Commercial Belt, Alpha 1",
                address = "Shop 14, Commercial Belt, Near Metro Station, Alpha 1, Greater Noida - 201310",
                rating = 4.8,
                reviewsCount = 428,
                distanceKm = 0.7,
                estimatedDeliveryMinutes = 15,
                isOpen = true,
                licenseNo = "UP-GN-2023-A1-0482",
                coordinates = Coordinates(28.4738, 77.5092),
                medicines = generateMedicinesForPharmacy("sharma", "Sharma Medical Store", "Alpha 1")
            )
        )
        list.add(
            Pharmacy(
                id = "pharm-alpha1-apollo",
                name = "Apollo Pharmacy Alpha 1",
                branch = "Pocket A Complex, Alpha 1",
                address = "Plot 8, Pocket A, Commercial Complex, Sector Alpha 1, Greater Noida - 201310",
                rating = 4.7,
                reviewsCount = 312,
                distanceKm = 1.1,
                estimatedDeliveryMinutes = 18,
                isOpen = true,
                licenseNo = "UP-GN-2022-A1-1029",
                coordinates = Coordinates(28.4750, 77.5110),
                medicines = generateMedicinesForPharmacy("apollo-a1", "Apollo Pharmacy Alpha 1", "Alpha 1")
            )
        )
        list.add(
            Pharmacy(
                id = "pharm-alpha1-medplus",
                name = "MedPlus Alpha 1",
                branch = "Metro Station Road, Alpha 1",
                address = "Shop 3, Ground Floor, Metro Arcade, Sector Alpha 1, Greater Noida - 201310",
                rating = 4.6,
                reviewsCount = 275,
                distanceKm = 1.4,
                estimatedDeliveryMinutes = 20,
                isOpen = true,
                licenseNo = "UP-GN-2024-A1-3391",
                coordinates = Coordinates(28.4722, 77.5075),
                medicines = generateMedicinesForPharmacy("medplus-a1", "MedPlus Alpha 1", "Alpha 1")
            )
        )

        // 2. Alpha 2 Pharmacies (Exact 2 Pharmacies)
        list.add(
            Pharmacy(
                id = "pharm-alpha2-wellness",
                name = "Wellness Forever Alpha 2",
                branch = "Main Sector Market, Alpha 2",
                address = "Shop 8, Main Sector Market, Near Community Center, Alpha 2, Greater Noida - 201310",
                rating = 4.9,
                reviewsCount = 512,
                distanceKm = 1.8,
                estimatedDeliveryMinutes = 22,
                isOpen = true,
                licenseNo = "UP-GN-2023-A2-9901",
                coordinates = Coordinates(28.4775, 77.5145),
                medicines = generateMedicinesForPharmacy("wellness-a2", "Wellness Forever Alpha 2", "Alpha 2")
            )
        )
        list.add(
            Pharmacy(
                id = "pharm-alpha2-sanjeevani",
                name = "Sanjeevani Chemist Alpha 2",
                branch = "Pocket F Complex, Alpha 2",
                address = "G-24, Shopping Complex, Pocket F, Alpha 2, Greater Noida - 201310",
                rating = 4.5,
                reviewsCount = 198,
                distanceKm = 2.2,
                estimatedDeliveryMinutes = 25,
                isOpen = true,
                licenseNo = "UP-GN-2021-A2-5402",
                coordinates = Coordinates(28.4790, 77.5160),
                medicines = generateMedicinesForPharmacy("sanjeevani-a2", "Sanjeevani Chemist Alpha 2", "Alpha 2")
            )
        )

        // 3. Registered Pharmacies across Sectors: Alpha 1 (3), Alpha 2 (2), Knowledge Park 2 (2), Sector 22 (1), Sector 76 (1), and more
        data class SectorAnchor(val sector: String, val addressFormat: String, val coords: Coordinates)

        val sectorAnchors = listOf(
            SectorAnchor("Knowledge Park 2", "Retail %d, Campus Metro Hub, Knowledge Park 2, Greater Noida - 201310", Coordinates(28.4650, 77.4950)),
            SectorAnchor("Knowledge Park 2", "SCO %d, College Commercial Belt, Knowledge Park 2, Greater Noida - 201310", Coordinates(28.4625, 77.4920)),
            SectorAnchor("Sector 22", "Shop %d, Sector 22 Central Market, Near Spice World, Greater Noida / Noida - 201301", Coordinates(28.5200, 77.4700)),
            SectorAnchor("Sector 76", "Shop %d, Amrapali Silicon Arcade, Sector 76, Noida - 201301", Coordinates(28.5600, 77.3900)),
            SectorAnchor("Beta 1", "Shop %d, Beta Plaza Commercial Centre, Sector Beta 1, Greater Noida - 201308", Coordinates(28.4820, 77.5020)),
            SectorAnchor("Beta 2", "SCO %d, Main Market, Pocket B, Sector Beta 2, Greater Noida - 201308", Coordinates(28.4845, 77.5045)),
            SectorAnchor("Gamma 1", "Booth %d, Shopping Arcade, Sector Gamma 1, Greater Noida - 201308", Coordinates(28.4860, 77.5080)),
            SectorAnchor("Gamma 2", "Shop %d, Ryan Circle Market, Sector Gamma 2, Greater Noida - 201308", Coordinates(28.4880, 77.5100)),
            SectorAnchor("Delta 1", "G-%d, Delta 1 Central Market, Sector Delta 1, Greater Noida - 201308", Coordinates(28.4900, 77.5130)),
            SectorAnchor("Delta 2", "Plot %d, Commercial Complex, Sector Delta 2, Greater Noida - 201308", Coordinates(28.4920, 77.5160)),
            SectorAnchor("Pari Chowk", "Shop %d, Near Ansal Plaza Mall, Pari Chowk, Greater Noida - 201310", Coordinates(28.4680, 77.5150)),
            SectorAnchor("Omega 1", "SCO %d, Omega Shopping Complex, Sector Omega 1, Greater Noida - 201310", Coordinates(28.4620, 77.5200)),
            SectorAnchor("Pi 1", "Shop %d, Sector Pi 1 Market, Greater Noida - 201310", Coordinates(28.4550, 77.5250)),
            SectorAnchor("Chi 2", "Shop %d, Eldeco Arcade, Sector Chi 2, Greater Noida - 201310", Coordinates(28.4520, 77.5280)),
            SectorAnchor("Knowledge Park 3", "Shop %d, Campus Commercial Zone, Knowledge Park 3, Greater Noida - 201310", Coordinates(28.4700, 77.4900)),
            SectorAnchor("Jagat Farm", "Shop %d, Central Market Jagat Farm, Greater Noida - 201308", Coordinates(28.4800, 77.5050)),
            SectorAnchor("Surajpur", "Main Road, Near Police Line, Surajpur, Greater Noida - 201306", Coordinates(28.5100, 77.4800)),
            SectorAnchor("Ecotech 3", "Plot %d, Commercial Plaza, Ecotech 3, Greater Noida - 201306", Coordinates(28.5300, 77.4600)),
            SectorAnchor("Sector 50", "Central Market, Sector 50, Noida - 201301", Coordinates(28.5700, 77.3700)),
            SectorAnchor("Sector 137", "Commercial Complex, Sector 137, Noida - 201305", Coordinates(28.5050, 77.4050))
        )

        val brandNames = listOf("Guardian Pharmacy", "Frank Ross Pharmacy", "Fortis HealthWorld", "Noble Chemist", "LifeCare Pharmacy", "Max Health Chemist", "City Medicos", "Sethi Medical Hall", "Gupta Drug Store", "Bansal Medicals", "Aarogyam Medicos", "Aggarwal Chemist")

        var pharmaIndex = 6
        while (list.size < 52) {
            val anchor = sectorAnchors[(pharmaIndex - 6) % sectorAnchors.size]
            val brand = brandNames[(pharmaIndex * 3) % brandNames.size]
            val shopNum = 10 + (pharmaIndex * 7) % 80
            val fullAddr = String.format(Locale.US, anchor.addressFormat, shopNum)
            val dist = kotlin.math.round(com.quickmeds24.app.utils.GeoLocationUtils.calculateDistanceKm(ALPHA_1_COORDS, anchor.coords) * 10.0) / 10.0
            val deliveryMins = com.quickmeds24.app.utils.GeoLocationUtils.calculateDeliveryMinutes(dist)
            val roundedRating = kotlin.math.round((4.2 + ((pharmaIndex % 8) * 0.1)) * 10.0) / 10.0

            list.add(
                Pharmacy(
                    id = "pharm-$pharmaIndex",
                    name = "$brand ${anchor.sector}",
                    branch = anchor.sector,
                    address = fullAddr,
                    rating = roundedRating,
                    reviewsCount = 80 + (pharmaIndex * 15),
                    distanceKm = dist,
                    estimatedDeliveryMinutes = deliveryMins,
                    isOpen = true,
                    licenseNo = "UP-GN-2023-${anchor.sector.take(2).uppercase()}-$pharmaIndex",
                    coordinates = anchor.coords,
                    medicines = generateMedicinesForPharmacy("ph-$pharmaIndex", "$brand ${anchor.sector}", anchor.sector)
                )
            )
            pharmaIndex++
        }

        return list
    }

    private fun categorizeMedicine(name: String): String {
        val n = name.lowercase()
        return when {
            n.contains("telma") || n.contains("amlong") || n.contains("cardivas") || n.contains("envas") -> "Heart Care"
            n.contains("glycomet") || n.contains("janumet") || n.contains("galvus") || n.contains("forxiga") || n.contains("strips") -> "Diabetes"
            n.contains("augmentin") || n.contains("clavam") || n.contains("azithral") || n.contains("ciplox") || n.contains("zifi") -> "Antibiotics"
            n.contains("pan") || n.contains("omez") || n.contains("razo") || n.contains("digene") -> "Digestive"
            n.contains("montair") || n.contains("allegra") || n.contains("cetzine") || n.contains("cough") || n.contains("sinarest") -> "Respiratory & Cold"
            n.contains("becosules") || n.contains("shelcal") || n.contains("limcee") || n.contains("zincovit") || n.contains("evion") -> "Vitamins & Supplements"
            n.contains("betadine") || n.contains("soframycin") || n.contains("volini") || n.contains("moov") || n.contains("candid") -> "First Aid & Pain"
            else -> "General Health"
        }
    }

    private fun isPrescriptionNeeded(name: String): Boolean {
        val n = name.lowercase()
        return n.contains("augmentin") || n.contains("azithral") || n.contains("clavam") || 
               n.contains("telma") || n.contains("glycomet") || n.contains("ciplox") || 
               n.contains("janumet") || n.contains("tramadol")
    }

    private fun getMedicineImageUrl(name: String): String {
        val n = name.lowercase()
        return when {
            n.contains("telma") -> "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=240"
            n.contains("dolo") || n.contains("calpol") || n.contains("crocin") -> "https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=240"
            n.contains("glycomet") -> "https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=240"
            n.contains("syrup") || n.contains("cough") -> "https://images.unsplash.com/photo-1631549916768-4119b2e5f926?w=240"
            n.contains("drops") -> "https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?w=240"
            n.contains("ointment") || n.contains("gel") -> "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=240"
            n.contains("strips") || n.contains("thermometer") -> "https://images.unsplash.com/photo-1603398938378-e54eab446dde?w=240"
            else -> "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=240"
        }
    }
}

