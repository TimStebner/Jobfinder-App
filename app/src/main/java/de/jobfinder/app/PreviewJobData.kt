package de.jobfinder.app

import de.jobfinder.app.core.model.EmploymentType
import de.jobfinder.app.core.model.Job
import de.jobfinder.app.core.model.JobSource
import de.jobfinder.app.core.model.Salary
import de.jobfinder.app.core.model.WorkMode

object PreviewJobData {

    val DetailJob = Job(
        id = "job-detail-si",
        title = "Fachinformatiker/in für Systemintegration - Ausbildungsplatz 2026 (m/w/d)",
        company = "IT Systems & Solutions GmbH",
        location = "Siegburg",
        postalCode = "53721",
        distanceKm = 4.0,
        postedDateText = "Vor 2 Tagen",
        description = "Als Fachinformatiker für Systemintegration planst und konfigurierst du IT-Systeme. Bei uns lernst du modernste Servertechnologien, Netzwerkarchitekturen und Cloud-Lösungen kennen.",
        employmentType = EmploymentType.INTERNSHIP,
        workMode = WorkMode.ONSITE,
        salary = Salary(
            displayText = "1.050 € – 1.250 € / Monat",
            minAmount = 1050.0,
            maxAmount = 1250.0,
            currency = "EUR",
            period = "MONTHLY"
        ),
        sources = listOf(JobSource.ARBEITSAGENTUR),
        primarySource = JobSource.ARBEITSAGENTUR,
        skills = listOf("Linux", "Windows Server", "Netzwerktechnik", "Cloud", "Hardware"),
        benefits = listOf(
            "Attraktive Ausbildungsvergütung (1.050 € - 1.250 €)",
            "Eigenes Firmen-Laptop & modernes Smartphone",
            "Sehr hohe Übernahmequote (über 90%)",
            "Kostenloses Jobticket & Fahrtkostenzuschuss",
            "30 Tage Urlaub & flexible Arbeitszeiten"
        ),
        qualifications = listOf(
            "Guter Realschulabschluss oder Abitur",
            "Begeisterung für IT, Hardware und moderne Netzwerke",
            "Erste Erfahrungen mit Linux oder Windows Server von Vorteil",
            "Teamfähigkeit und logisches Denkvermögen"
        ),
        applicationUrl = null,
        sourceUrl = null
    )

    val PopularJobs = listOf(
        Job(
            id = "job-pop-1",
            title = "Junior Java Developer",
            company = "FinTech Hub",
            location = "Berlin",
            postalCode = "10115",
            distanceKm = 15.0,
            postedDateText = "Vor 1 Tag",
            description = "Entwicklung zukunftssicherer Bankingsysteme mit Java und Spring Boot.",
            employmentType = EmploymentType.FULL_TIME,
            workMode = WorkMode.ONSITE,
            salary = Salary(
                displayText = "45.000 € – 55.000 €",
                minAmount = 45000.0,
                maxAmount = 55000.0,
                currency = "EUR",
                period = "YEARLY"
            ),
            sources = listOf(JobSource.STEPSTONE),
            primarySource = JobSource.STEPSTONE,
            skills = listOf("Java", "Spring Boot", "SQL"),
            benefits = listOf("30 Tage Urlaub", "Weiterbildungsbudget"),
            qualifications = listOf("Bachelor Informatik oder vergleichbar"),
            applicationUrl = null,
            sourceUrl = null
        ),
        Job(
            id = "job-pop-2",
            title = "Fullstack Developer",
            company = "Creative Media",
            location = "München",
            postalCode = "80331",
            distanceKm = 0.0,
            postedDateText = "Vor 3 Tagen",
            description = "Erstellung moderner Webapplikationen in agilen Teams.",
            employmentType = EmploymentType.FULL_TIME,
            workMode = WorkMode.REMOTE,
            salary = Salary(
                displayText = "60.000 € – 75.000 €",
                minAmount = 60000.0,
                maxAmount = 75000.0,
                currency = "EUR",
                period = "YEARLY"
            ),
            sources = listOf(JobSource.ARBEITSAGENTUR),
            primarySource = JobSource.ARBEITSAGENTUR,
            skills = listOf("TypeScript", "React", "Node.js"),
            benefits = listOf("Remote-First", "Fitness-Mitgliedschaft"),
            qualifications = listOf("Erfahrung mit React und TypeScript"),
            applicationUrl = null,
            sourceUrl = null
        )
    )

    val FavoriteJobs = listOf(
        Job(
            id = "job-fav-google",
            title = "Sr Backend Engineer",
            company = "Google",
            location = "Mountain View, CA",
            postalCode = null,
            distanceKm = null,
            postedDateText = "Vor 3 Tagen",
            description = "Building planetary scale distributed systems.",
            employmentType = EmploymentType.FULL_TIME,
            workMode = WorkMode.REMOTE,
            salary = Salary(
                displayText = "180.000 € – 220.000 € Jahresgehalt",
                minAmount = 180000.0,
                maxAmount = 220000.0,
                currency = "EUR",
                period = "YEARLY"
            ),
            sources = listOf(JobSource.STEPSTONE),
            primarySource = JobSource.STEPSTONE,
            skills = listOf("Python", "Microservices", "PostgreSQL", "Go", "Distributed Systems", "Kubernetes"),
            benefits = listOf("Comprehensive Healthcare", "Stock Grants", "Free Meals"),
            qualifications = listOf("BS/MS Computer Science", "7+ years backend experience"),
            applicationUrl = null,
            sourceUrl = null
        ),
        Job(
            id = "job-fav-apple",
            title = "Product Design Intern",
            company = "Apple",
            location = "Cupertino, CA",
            postalCode = null,
            distanceKm = null,
            postedDateText = "Vor 2 Tagen",
            description = "Create extraordinary user experiences for the next billion devices.",
            employmentType = EmploymentType.INTERNSHIP,
            workMode = WorkMode.ONSITE,
            salary = null,
            sources = listOf(JobSource.STEPSTONE),
            primarySource = JobSource.STEPSTONE,
            skills = listOf("Figma", "Design Systems", "iOS"),
            benefits = listOf("Housing stipend", "Product discounts"),
            qualifications = listOf("Enrolled in Design or HCI program"),
            applicationUrl = null,
            sourceUrl = null
        ),
        Job(
            id = "job-fav-spotify",
            title = "Data Scientist",
            company = "Spotify",
            location = "Berlin",
            postalCode = "10115",
            distanceKm = 10.0,
            postedDateText = "Vor 5 Tagen",
            description = "Derive insights from billions of music streaming events.",
            employmentType = EmploymentType.FULL_TIME,
            workMode = WorkMode.HYBRID,
            salary = Salary(
                displayText = "85.000 € – 100.000 €",
                minAmount = 85000.0,
                maxAmount = 100000.0,
                currency = "EUR",
                period = "YEARLY"
            ),
            sources = listOf(JobSource.ARBEITSAGENTUR),
            primarySource = JobSource.ARBEITSAGENTUR,
            skills = listOf("Python", "Machine Learning", "BigQuery"),
            benefits = listOf("All the music you want", "Learning budget"),
            qualifications = listOf("Master in Data Science or Statistics"),
            applicationUrl = null,
            sourceUrl = null
        )
    )

    val OnboardingFeaturedJob = Job(
        id = "job-onboard-amazon",
        title = "Senior Fullstack Engineer",
        company = "Amazon",
        location = "München & Remote",
        postalCode = "80539",
        distanceKm = null,
        postedDateText = "Neu",
        description = "Develop customer-obsessed scalable architectures.",
        employmentType = EmploymentType.FULL_TIME,
        workMode = WorkMode.HYBRID,
        salary = Salary(
            displayText = "75.000 € – 92.000 € · Hybrid",
            minAmount = 75000.0,
            maxAmount = 92000.0,
            currency = "EUR",
            period = "YEARLY"
        ),
        sources = listOf(JobSource.STEPSTONE),
        primarySource = JobSource.STEPSTONE,
        skills = listOf("Java", "AWS", "React", "DynamoDB"),
        benefits = listOf("Sign-on bonus", "RSUs"),
        qualifications = listOf("5+ years software engineering"),
        applicationUrl = null,
        sourceUrl = null
    )

    fun createFiftySearchResults(): List<Job> {
        val list = mutableListOf<Job>()
        // 1. Primary Stitch job
        list.add(DetailJob)

        // 2. TechNova Senior Java
        list.add(
            Job(
                id = "job-search-technova",
                title = "Senior Java Backend Entwickler (m/w/d)",
                company = "TechNova Solutions",
                location = "Köln",
                postalCode = "50667",
                distanceKm = 22.0,
                postedDateText = "Vor 1 Tag",
                description = "Modernste Cloud-Native Entwicklung auf AWS und Kubernetes.",
                employmentType = EmploymentType.FULL_TIME,
                workMode = WorkMode.HYBRID,
                salary = Salary(
                    displayText = "75.000 € – 90.000 €",
                    minAmount = 75000.0,
                    maxAmount = 90000.0,
                    currency = "EUR",
                    period = "YEARLY"
                ),
                sources = listOf(JobSource.STEPSTONE),
                primarySource = JobSource.STEPSTONE,
                skills = listOf("Java", "Spring Boot", "Microservices", "Docker", "AWS", "Kubernetes", "Git"),
                benefits = listOf("30 Tage Urlaub", "Home Office", "Jobrad"),
                qualifications = listOf("Mehrjährige Erfahrung mit Spring Boot"),
                applicationUrl = null,
                sourceUrl = null
            )
        )

        // 3. Junior IT-Administrator
        list.add(
            Job(
                id = "job-search-admin",
                title = "Junior IT-Administrator (m/w/d)",
                company = "Rhein-Sieg Datennetze",
                location = "Bonn",
                postalCode = "53111",
                distanceKm = 15.0,
                postedDateText = "Vor 3 Tagen",
                description = "Betreuung der internen IT-Infrastruktur und Anwendersupport.",
                employmentType = EmploymentType.FULL_TIME,
                workMode = WorkMode.ONSITE,
                salary = Salary(
                    displayText = "42.000 € – 48.000 €",
                    minAmount = 42000.0,
                    maxAmount = 48000.0,
                    currency = "EUR",
                    period = "YEARLY"
                ),
                sources = listOf(JobSource.KLEINANZEIGEN),
                primarySource = JobSource.KLEINANZEIGEN,
                skills = listOf("Active Directory", "Helpdesk", "Hardware"),
                benefits = listOf("Weiterbildungsmöglichkeiten"),
                qualifications = listOf("Ausbildung zum Fachinformatiker oder vergleichbar"),
                applicationUrl = null,
                sourceUrl = null
            )
        )

        // Generate items 4 to 50
        val titles = listOf(
            "Systemadministrator Linux (m/w/d)",
            "Cloud Security Engineer (m/w/d)",
            "DevOps Engineer Kubernetes (m/w/d)",
            "Fachinformatiker Anwendungsentwicklung (m/w/d)",
            "IT Support Spezialist 1st/2nd Level (m/w/d)",
            "Netzwerktechniker Cisco (m/w/d)",
            "Database Administrator PostgreSQL (m/w/d)"
        )
        val companies = listOf(
            "Köln IT Services",
            "Bonn Digital Solutions",
            "Siegburg Systems AG",
            "Rheinland Data GmbH",
            "West IT Consult"
        )
        val locations = listOf("Köln", "Bonn", "Siegburg", "Troisdorf", "Sankt Augustin")
        val sourcesList = listOf(
            listOf(JobSource.STEPSTONE),
            listOf(JobSource.ARBEITSAGENTUR),
            listOf(JobSource.KLEINANZEIGEN),
            listOf(JobSource.STEPSTONE, JobSource.ARBEITSAGENTUR)
        )

        for (i in 4..50) {
            val title = titles[(i - 4) % titles.size]
            val company = companies[(i - 4) % companies.size]
            val loc = locations[(i - 4) % locations.size]
            val src = sourcesList[(i - 4) % sourcesList.size]
            list.add(
                Job(
                    id = "job-search-$i",
                    title = "$title #$i",
                    company = company,
                    location = loc,
                    postalCode = "53721",
                    distanceKm = (5 + (i * 2) % 25).toDouble(),
                    postedDateText = "Vor ${(i % 7) + 1} Tagen",
                    description = "Spannende Aufgaben bei $company im Raum $loc.",
                    employmentType = if (i % 3 == 0) EmploymentType.FULL_TIME else EmploymentType.PART_TIME,
                    workMode = if (i % 2 == 0) WorkMode.HYBRID else WorkMode.REMOTE,
                    salary = Salary(
                        displayText = "${40 + (i % 20) * 2}.000 € – ${50 + (i % 20) * 2}.000 €",
                        minAmount = (40000 + (i % 20) * 2000).toDouble(),
                        maxAmount = (50000 + (i % 20) * 2000).toDouble(),
                        currency = "EUR",
                        period = "YEARLY"
                    ),
                    sources = src,
                    primarySource = src.first(),
                    skills = listOf("Linux", "Docker", "Netzwerk"),
                    benefits = listOf("Flexoffice", "30 Tage Urlaub"),
                    qualifications = listOf("Berufserfahrung in der IT"),
                    applicationUrl = null,
                    sourceUrl = null
                )
            )
        }

        return list
    }
}
