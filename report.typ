#import "template.typ": *
#show: project.with(
  title: "DAA - Laboratoire 3",
  authors: (
    "Émilie Bressoud",
    "Sacha Butty",
    "Loïc Herman"
  ),
  date: "November 01, 2024",
) 


/* 
Veuillez répondre aux 5 questions suivantes. Pour chacune d’entre elles, vous développerez votre
réponse et l’illustrerez par des extraits de code. */
= Réponses aux questions
== Question 4.1
_Pour le champ remark, destiné à accueillir un texte pouvant être plus long qu’une seule ligne,
quelle configuration particulière faut-il faire dans le fichier XML pour que son comportement
soit correct ? Nous pensons notamment à la possibilité de faire des retours à la ligne, d’activer
le correcteur orthographique et de permettre au champ de prendre la taille nécessaire._

Il faut ajouter les attributs suivants au champ `EditText` dans le fichier XML :
```xml
android:inputType="textMultiLine|textAutoCorrect"
android:minLines="2"
android:layout_height="wrap_content"
```

Cela permet de définir le champ comme étant un champ multiligne et de définir le nombre de lignes affichées par défaut.
Par ailleurs, afin que le champs puisse s'adapter à la taille du texte, nous avons ajouté l'attribut `android:layout_height="wrap_content"`.
En ce qui concerne le correcteur orthographique, nous avons ajouté l'attribut `android:inputType="textAutoCorrect"`.

== Question 4.2
_Pour afficher la date sélectionnée via le DatePicker nous pouvons utiliser un DateFormat
permettant par exemple d’afficher 12 juin 1996 à partir d’une instance de Date. Le formatage
des dates peut être relativement différent en fonction des langues, la traduction des mois par
exemple, mais également des habitudes régionales différentes : la même date en anglais
britannique serait 12th June 1996 et en anglais américain June 12, 1996. Comment peut-on
gérer cela au mieux ?_

Pour gérer les différentes langues et les différentes habitudes régionales, nous pouvons utiliser la classe `SimpleDateFormat` en lui passant un `Locale` en paramètre.
```kotlin
val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE)
val formattedDate = dateFormat.format(date)
```

Par exemple, pour afficher la date en français, nous utilisons le `Locale.FRANCE` qui permet d'afficher les mois en français.
Pour afficher la date en anglais britannique, nous utiliserions le `Locale.UK` et pour l'anglais américain, le `Locale.US`.

Pour afficher la date en fonction de la langue de l'utilisateur, nous pouvons utiliser le `Locale.getDefault()` qui permet de récupérer la langue du téléphone.

== Question 4.3
_Veuillez choisir une question en fonction de votre choix d’implémentation :_
=== Question 4.3.a
_Si vous avez utilisé le DatePickerDialog1 du SDK. En cas de rotation de l’écran du
smartphone lorsque le dialogue est ouvert, une exception android.view.WindowLeaked
sera présente dans les logs, à quoi est-elle due ?_
=== Question 4.3.b
_Si vous avez utilisé le MaterialDatePicker2 de la librairie Material. Est-il possible de limiter
les dates sélectionnables dans le dialogue, en particulier pour une date de naissance il est
peu probable d’avoir une personne née il y a plus de 110 ans ou à une date dans le futur.
Comment pouvons-nous mettre cela en place ?_

Pour limiter les dates sélectionnables dans `MaterialDatePicker`, nous pouvons utiliser la méthode `setCalendarConstraints` qui permet de définir les limites de dates sélectionnables.

```kotlin
// création du builder
private fun createConstraintsBuilder(): CalendarConstraints {
    val today = MaterialDatePicker.todayInUtcMilliseconds()
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    calendar.timeInMillis = today
    calendar.add(Calendar.YEAR, -110)
    val minDate = calendar.timeInMillis

    return CalendarConstraints.Builder()
        .setStart(minDate)
        .setEnd(today)
        .build()
}

// Puis l'on ajoute le builder à notre MaterialDatePicker

val datePicker = MaterialDatePicker.Builder.datePicker()
    .setCalendarConstraints(constraintsBuilder.build())
    .build()
```



== Question 4.4
_Lors du remplissage des champs textuels, vous pouvez constater que le bouton « suivant »
présent sur le clavier virtuel permet de sauter automatiquement au prochain champ à saisir,
cf. Fig. 2. Est-ce possible de spécifier son propre ordre de remplissage du questionnaire ?
Arrivé sur le dernier champ, est-il possible de faire en sorte que ce bouton soit lié au bouton
de validation du questionnaire ?
Hint : Le champ remark, multilignes, peut provoquer des effets de bords en fonction du clavier
virtuel utilisé sur votre smartphone. Vous pouvez l’échanger avec le champ e-mail pour faciliter
vos recherches concernant la réponse à cette question._
// TODO

== Question 4.5
_Pour les deux Spinners (nationalité et secteur d’activité), comment peut-on faire en sorte que
le premier choix corresponde au choix null, affichant par exemple le label « Sélectionner » ?
Comment peut-on gérer cette valeur pour ne pas qu’elle soit confondue avec une réponse ?_

Pour afficher un choix par défaut dans un `Spinner`, il suffit d'abord d'ajouter dans la liste les strings `Sélectionner`
de la manière suivante :
```kotlin
// Exemple pour le spinner de nationalités, l'on ajoute le choix "Sélectionner" en premier
val nationalities = listOf(getString(R.string.nationality_empty)) + resources.getStringArray(R.array.nationalities
```

Ensuite, nous avons utilisé un `ArrayAdapter` pour afficher les éléments dans le `Spinner` et nous avons défini le premier élément comme étant le choix par défaut.
```kotlin
val adapter = CustomSpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item, nationalities)
```

Afin de pouvoir modifier l'apparence du choix par défaut, nous avons créé un `CustomSpinnerAdapter`. Celui ci permet de changer le dropdown view pour afficher le choix par défaut de manière différente.
Il suffit de surcharger la méthode `getDropDownView` pour définir le style du choix par défaut. Cette méthode est appelée pour chaque élément du dropdown.
Il est donc possible de vérifier si l'élément est le premier de la liste pour le cacher.
