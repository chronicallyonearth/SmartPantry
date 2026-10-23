# Smart Pantry Manager

A native Android application built with Java that helps users manage pantry ingredients, monitor expiry dates, and discover recipes they can prepare using the ingredients they have available.

## 1. Project Overview

Smart Pantry Manager aims to make household ingredient management easier by allowing users to record pantry items and receive recipe suggestions based on ingredient availability.

The application's main rule is that a recipe is suggested only when every required ingredient is available in a sufficient quantity.

## 2. Objectives

- Create, view, update, and delete pantry ingredients.
- Store ingredient names, quantities, measurement units, and optional expiry dates.
- Suggest recipes based on available ingredients.
- Prevent recipes from being suggested when required ingredients are missing or insufficient.
- Display recipe ingredients and cooking instructions.
- Provide an option to enable or disable expiry alerts.
- Retain pantry information between application sessions.

## 3. Technologies Used

- **Language:** Java
- **IDE:** Android Studio
- **Database:** SQLite using `SQLiteOpenHelper`
- **List interface:** RecyclerView with custom adapters
- **Settings storage:** SharedPreferences
- **User interface:** XML layouts and Material Components
- **Minimum Android SDK:** API 26

## 4. Main Features

### Pantry Management
Users can add, edit, and delete ingredients. Each item can include a name, quantity, measurement unit, and optional expiry date.

### Recipe Suggestions
The application compares recipe requirements against pantry contents. Recipes are excluded when ingredients are missing or available quantities are insufficient.

### Measurement Handling
Supported units include grams, kilograms, millilitres, litres, and pieces. Compatible units are converted to a common base unit before quantities are compared.

### Expiry Alerts
Users can enable or disable the pantry expiry banner. The application identifies ingredients that have expired or are due to expire within three days.

### Recipe Details
Users can open a suggested recipe to view its required ingredients and preparation instructions.

## 5. Screens

1. **Pantry:** Displays saved ingredients and provides access to the main features.
2. **Add/Edit Ingredient:** Allows users to enter or modify ingredient information.
3. **Suggested Recipes:** Displays recipes that satisfy the matching rules.
4. **Recipe Details:** Shows recipe ingredients and instructions.
5. **Settings:** Provides the expiry-alert preference.

## 6. How Recipe Matching Works

The matching algorithm checks each required recipe ingredient against the pantry.

- Ingredient names are normalised to handle differences in capitalisation, whitespace, and selected plural forms.
- Compatible units are converted before comparing quantities.
- Duplicate pantry entries for an ingredient can be combined.
- Missing ingredients cause the recipe to be rejected.
- Insufficient quantities cause the recipe to be rejected.
- Additional pantry ingredients do not prevent a recipe from matching.

For example, a recipe requiring 200 g of flour can be matched against 500 g of flour. However, 100 g is insufficient, and 200 g of flour cannot satisfy a requirement for 200 ml of milk.

## 7. Installation and Setup

### Requirements

- Android Studio
- Android SDK and required build tools
- Java/Gradle environment configured through Android Studio
- An Android emulator or compatible Android device

### Steps

1. Clone or download the project repository.
2. Open the project in Android Studio.
3. Allow Gradle to synchronise and download required dependencies.
4. Start an Android emulator or connect a device with USB debugging enabled.
5. Select the device in Android Studio.
6. Run the application using the Run button.

## 8. Testing

The following tests should be performed before submission:

- Add, edit, and delete pantry ingredients.
- Check that invalid names and quantities are rejected.
- Close and reopen the app to verify data persistence.
- Confirm that recipes with missing ingredients are excluded.
- Confirm that insufficient quantities prevent recipe suggestions.
- Test compatible unit conversions, such as kilograms to grams.
- Test that incompatible measurement categories are not treated as equivalent.
- Verify that ingredient-name normalisation works for supported plural forms.
- Open recipe details and return to the recipe list.
- Toggle expiry alerts and check the pantry banner.
- Test screen rotation and navigation for stability.

Automated recipe-matching tests can be run with:

```bash
./gradlew testDebugUnitTest
```

**Testing status:

BUILD SUCCESSFUL in 5s
22 actionable tasks: 7 executed, 15 up-to-date
Configuration cache entry stored.

**RecipeMatcherTest.java

Executing tasks: [:app:testDebugUnitTest, --tests, com.example.smartpantry.RecipeMatcherTest] in project /home/void/AndroidStudioProjects/SmartPantry

Reusing configuration cache.
> Task :app:preBuild UP-TO-DATE
> Task :app:preDebugBuild UP-TO-DATE
> Task :app:preDebugUnitTestBuild UP-TO-DATE
> Task :app:mapDebugSourceSetPaths UP-TO-DATE
> Task :app:javaPreCompileDebug UP-TO-DATE
> Task :app:createDebugCompatibleScreenManifests UP-TO-DATE
> Task :app:generateDebugResources UP-TO-DATE
> Task :app:extractDeepLinksDebug UP-TO-DATE
> Task :app:javaPreCompileDebugUnitTest UP-TO-DATE
> Task :app:packageDebugResources UP-TO-DATE
> Task :app:checkDebugAarMetadata UP-TO-DATE
> Task :app:processDebugNavigationResources UP-TO-DATE
> Task :app:mergeDebugResources UP-TO-DATE
> Task :app:compileDebugNavigationResources UP-TO-DATE
> Task :app:parseDebugLocalResources UP-TO-DATE
> Task :app:generateDebugRFile UP-TO-DATE
> Task :app:processDebugMainManifest UP-TO-DATE
> Task :app:processDebugManifest UP-TO-DATE
> Task :app:processDebugManifestForPackage UP-TO-DATE
> Task :app:compileDebugKotlin NO-SOURCE
> Task :app:processDebugJavaRes NO-SOURCE
> Task :app:compileDebugJavaWithJavac UP-TO-DATE
> Task :app:processDebugResources UP-TO-DATE
> Task :app:bundleDebugClassesToCompileJar UP-TO-DATE
> Task :app:bundleDebugClassesToRuntimeJar UP-TO-DATE
> Task :app:compileDebugUnitTestKotlin NO-SOURCE
> Task :app:processDebugUnitTestJavaRes NO-SOURCE
> Task :app:compileDebugUnitTestJavaWithJavac UP-TO-DATE
> Task :app:testDebugUnitTest
BUILD SUCCESSFUL in 955ms
22 actionable tasks: 1 executed, 21 up-to-date
Configuration cache entry reused.

Build Analyzer results available
17:31:46: Execution finished ':app:testDebugUnitTest --tests "com.example.smartpantry.RecipeMatcherTest"'.


## 9. Screenshots

1.Empty App pantry



2.Add Ingredients



3.Expiry Alert



4.Date Selector



5.Suggested Recipe



6.Kota Recipe





## 10. Limitations

- Recipe matching depends on ingredient names and a limited set of normalisation rules.
- The recipe collection is predefined rather than dynamically obtained from an external source.
- Ingredient quantities must be entered manually.
- The application does not automatically detect products or update pantry quantities when ingredients are consumed.
- Measurement conversion is limited to the supported units and categories.

## 11. Future Improvements

- Add ingredient search, filtering, and sorting.
- Expand the recipe collection and allow users to create their own recipes.
- Improve ingredient matching to handle synonyms and more complex plural forms.
- Add automatic shopping-list generation for missing recipe ingredients.
- Improve expiry-date notifications and ingredient-consumption tracking.
- Add backup and synchronisation options.

## 12. Author

- **Name:** Dipela Junior Mabe
- **Course:** Mobile App Development 700
- **Institution:** Richfield Graduate Institute of Technology
- **Submission date:** 23 October 2026
