# OptionalBuilder

`OptionalBuilder` est une bibliothèque Java utilitaire qui simplifie la manipulation et la combinaison de deux valeurs optionnelles. Elle permet de créer des opérations synchrones et asynchrones en utilisant des interfaces fonctionnelles, tout en garantissant un code clair et robuste.

## Fonctionnalités principales

- **Combinaison des valeurs optionnelles** : Combine deux valeurs optionnelles à l'aide de fonctions simples ou complexes.
- **Support asynchrone** : Gère des `CompletableFuture` pour des combinaisons non bloquantes.
- **Wrapper de résultats** : Fournit une classe `Result` pour encapsuler les valeurs combinées.
- **API fluide** : Simplifie la manipulation grâce à des méthodes statiques et des interfaces fonctionnelles.

## Prérequis

- **Java** : Version 21 ou supérieure
- **Lombok** : Nécessaire pour les annotations `@AllArgsConstructor`

## Installation

Ajoutez la bibliothèque à votre projet en la construisant localement ou en l'ajoutant à votre gestionnaire de dépendances (si publiée sur un repository public).

### Maven
```xml
<dependency>
    <groupId>fr.grelu</groupId>
    <artifactId>optional-builder</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```gradle
implementation 'fr.grelu:optional-builder:1.0.0'
```

## Utilisation

### Exemple de base : Combinaison synchronisée
```java
import fr.grelu.common.OptionalBuilder;

import java.util.Optional;

public class Example {
    public static void main(String[] args) {
        Optional<String> first = Optional.of("Hello");
        Optional<Integer> second = Optional.of(42);

        Optional<String> result = OptionalBuilder.of(first, second)
            .merge((str, num) -> str + " world, the answer is " + num);

        result.ifPresent(System.out::println);
        // Output : Hello world, the answer is 42
    }
}
```

### Exemple avancé : Gestion asynchrone
```java
import fr.grelu.common.OptionalBuilder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AsyncExample {
    public static void main(String[] args) {
        CompletableFuture<Optional<String>> first = CompletableFuture.supplyAsync(() -> Optional.of("Async"));
        CompletableFuture<Optional<Integer>> second = CompletableFuture.supplyAsync(() -> Optional.of(2025));

        OptionalBuilder<String, Integer> builder = OptionalBuilder.ofAsync(first, second);
        Optional<OptionalBuilder.Result<String, Integer>> result = builder.merge();

        result.ifPresent(r -> System.out.println(r.first() + " year is " + r.second()));
        // Output : Async year is 2025
    }
}
```

### API complète

#### `OptionalBuilder.of(Optional<E> first, Optional<F> second)`
Crée un `OptionalBuilder` à partir de deux valeurs optionnelles.

#### `OptionalBuilder.ofAsync(CompletableFuture<Optional<E>> first, CompletableFuture<Optional<F>> second)`
Crée un `OptionalBuilder` en combinant deux futures asynchrones.

#### `merge(BiFunction<E, F, T> combiner)`
Combine les deux valeurs avec une fonction simple.

#### `complexMerge(BiFunction<E, F, Optional<T>> combiner)`
Combine les deux valeurs avec une fonction complexe.

#### `merge()`
Renvoie un `Optional` contenant un `Result` si les deux valeurs sont présentes.

#### `Result<E, F>`
Une classe wrapper contenant les deux valeurs combinées.

## Licence

Ce projet est distribué sous la licence MIT. Consultez le fichier [LICENSE](LICENSE) pour plus d'informations.

## Auteur

Dorian (2025)
