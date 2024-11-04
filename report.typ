#import "template.typ": *
#show: project.with(
  title: "DAA - Laboratoire 3",
  authors: (
    "Émilie Bressoud",
    "Sacha Butty",
    "Loïc Herman"
  ),
  date: "November 04, 2024",
) 

/* 
Veuillez répondre aux 5 questions suivantes. Pour chacune d’entre elles, vous développerez votre
réponse et l’illustrerez par des extraits de code. */
= Réponses aux questions
== Question 4.1
- _Pour le champ remark, destiné à accueillir un texte pouvant être plus long qu’une seule ligne,
quelle configuration particulière faut-il faire dans le fichier XML pour que son comportement
soit correct ? Nous pensons notamment à la possibilité de faire des retours à la ligne, d’activer
le correcteur orthographique et de permettre au champ de prendre la taille nécessaire._

Il faut ajouter les attributs suivants au champ `EditText` dans le fichier XML :
```xml
<!-- Fait en sorte que le texte soit aligné du haut du champ -->
android:gravity="top"
<!-- Indique à Android que le champ est multi-lignes et peut être auto-corrigé -->
android:inputType="textMultiLine|textAutoCorrect"
<!-- Affiche 3 lignes d'espace vide et grandira en fonction du contenu -->
android:minLines="3"
```

Cela permet de définir le champ comme étant un champ multiligne et de définir le nombre de lignes affichées par défaut.
En ce qui concerne le correcteur orthographique, nous avons ajouté l'attribut `android:inputType="textAutoCorrect"`.

== Question 4.2
- _Pour afficher la date sélectionnée via le DatePicker nous pouvons utiliser un DateFormat
permettant par exemple d’afficher 12 juin 1996 à partir d’une instance de Date. Le formatage
des dates peut être relativement différent en fonction des langues, la traduction des mois par
exemple, mais également des habitudes régionales différentes : la même date en anglais
britannique serait 12th June 1996 et en anglais américain June 12, 1996. Comment peut-on
gérer cela au mieux ?_

Pour gérer les différentes langues et les différentes habitudes régionales, nous initialisons l'instance de `DateFormat` du companion object de `Person` qui utilise la locale par défaut du système (via l'appel à `getDateInstance()`). Nous avons également défini la timezone du formatter à `UTC` pour éviter les problèmes de décalage lors de la conversion en epoch UTC pour le date picker.
```kotlin
companion object {
  private val utcTimezone: TimeZone = TimeZone.getTimeZone("UTC")
  private val utcDateFormatter = Person.dateFormatter.apply { timeZone = utcTimezone }
}
```

Ainsi, la langue de la date affichée dépendra de la langue configurée sur le téléphone et suivra les habitudes régionales.

== Question 4.3
- _Si vous avez utilisé le MaterialDatePicker2 de la librairie Material. Est-il possible de limiter
les dates sélectionnables dans le dialogue, en particulier pour une date de naissance il est
peu probable d’avoir une personne née il y a plus de 110 ans ou à une date dans le futur.
Comment pouvons-nous mettre cela en place ?_

Pour limiter les dates sélectionnables dans `MaterialDatePicker`, nous pouvons utiliser la méthode `setCalendarConstraints` qui permet de définir les limites de dates sélectionnables. Nous ajoutons dans ces contraintes un validateur qui validera que la date soit dans le passé, ainsi que les dates de début et de fin pour limiter la sélection à 110 ans en arrière jusqu'à aujourd'hui.

Nous avons également défini la sélection à la date actuelle si une date est déjà définie ou le jour courant le cas échéant.

```kotlin
val datePicker = MaterialDatePicker.Builder.datePicker()
  .setCalendarConstraints(
    CalendarConstraints.Builder()
      // Make the date picker start at the current date
      .setOpenAt(currentTimestamp)
      // Validate the date is in the past
      .setValidator(DateValidatorPointBackward.now())
      // Restrict the date picker from 110 years ago to today
      .setStart(utcCalendar.also { it.add(Calendar.YEAR, -110) }.timeInMillis)
      .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
      .build()
  )
  // Select the current date if the input field is not empty
  .setSelection(currentTimestamp)
  .build()
```

== Question 4.4
- _Lors du remplissage des champs textuels, vous pouvez constater que le bouton « suivant »
présent sur le clavier virtuel permet de sauter automatiquement au prochain champ à saisir,
cf. Fig. 2. Est-ce possible de spécifier son propre ordre de remplissage du questionnaire ?
Arrivé sur le dernier champ, est-il possible de faire en sorte que ce bouton soit lié au bouton
de validation du questionnaire ?_

Oui, l'ordre peut être configuré en utilisant l'attribut `android:imeOptions` pour donner l'indication au clavier virtuel pour le type d'action qui doit être effectué avec le bouton "suivant". Nous l'avons par exemple utilisé pour le champ du prénom pour que le focus soit transféré au bouton d'ouverture du `DatePicker` après avoir appuyé sur "suivant", avec un `focusChangeListener` sur le bouton permettant l'ouverture automatique du sélecteur.

Pour spécifier un ordre de remplissage personalisé, il est aussi possible d'utiliser l'attribut `android:nextFocusDown` pour indiquer le champ suivant à sélectionner lorsque le focus se déplace dans la direction `View.FOCUS_DOWN` (qui est la direction par défaut). L'attibut doit faire référence à l'ID du champ suivant. Il existe des variantes de cet attibut pour toutes les directions de focus possibles.

Pour que le champ des commentaires affiche le bouton "suivant" comme bouton de fin, nous avons spécifié l'attribut `android:imeOptions="actionDone"` pour indiquer que le bouton "suivant" doit être remplacé par un bouton "done" qui permet de valider le formulaire. Avec ceci nous avons ajouté un `OnEditorActionListener` sur le champ pour intercepter l'action "done" et automatiquement cliquer sur le bouton de validation du formulaire.

```kotlin
binding.inputComments.setOnEditorActionListener { _, action, _ ->
  if (action == EditorInfo.IME_ACTION_DONE) {
    binding.buttonOk.performClick()
    true
  } else {
    false
  }
}
```

== Question 4.5
- _Pour les deux Spinners (nationalité et secteur d’activité), comment peut-on faire en sorte que
le premier choix corresponde au choix null, affichant par exemple le label « Sélectionner » ?
Comment peut-on gérer cette valeur pour ne pas qu’elle soit confondue avec une réponse ?_

Pour afficher un choix par défaut dans un `Spinner`, nous avons créé un `Adapter` spécifique pour le `Spinner` qui permet de gérer l'affichage du choix par défaut. Le constructeur de ce dernier prend en paramètre le texte de la valeur par défaut et le tableau des valeurs possibles.
```kotlin
binding.inputNationality.adapter = SpinnerDefaultValueAdapter(
  this,
  resources.getString(R.string.nationality_empty),
  resources.getStringArray(R.array.nationalities)
)
```

Cet adapter va ajouter la valeur par défaut en première position de la liste des valeurs possibles. La méthode `getDropDownView` est surchargée pour désactiver l'affichage de la première valeur dans le dropdown.

Nous surchargons également la méthode `isEnabled(int position)` pour empêcher la sélection de la première valeur dans le `Spinner`.

```kotlin
override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
  // Hides the first item (default value) in the dropdown list
  if (position == 0) {
    val v = View(context)
    v.visibility = View.GONE
    return v
  }

  return super.getDropDownView(position, null, parent)
}

override fun isEnabled(position: Int): Boolean {
  // Sets the first item (default value) as non-selectable
  return position != 0 && super.isEnabled(position)
}
```

Ces deux surcharges feront que le choix n'est pas visible est non-sélectionnable dans le `Spinner`, permettant d'éviter toute confusion tout en gardant l'affichage de la valeur par défaut quand le champ est dans son état initial (position 0).


= Description de l'implémentation
