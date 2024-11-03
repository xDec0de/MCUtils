plugins {
	mcutils.`shadow-conventions`
	mcutils.`library-conventions`
}

repositories {
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	api(project(":shared"))
	api(libs.adventure)
	api(libs.adventure.serializer.legacy)
	api(libs.snakeyaml)
}
