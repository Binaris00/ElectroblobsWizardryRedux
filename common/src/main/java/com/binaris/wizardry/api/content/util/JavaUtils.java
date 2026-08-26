package com.binaris.wizardry.api.content.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Contains useful static methods for interacting with Java itself, rather than Minecraft. These methods used to be part
 * of {@code WizardryUtilities}.
 *
 * @author Electroblob
 */
public class JavaUtils {

    /**
     * Flattens the given nested collection. The returned collection is an unmodifiable collection of all the elements
     * contained within all of the sub-collections of the given nested collection.
     * @param collection A nested collection to flatten
     * @param <E> The type of elements in the given nested collection
     * @return The resulting flattened collection.
     */
    public static <E> Collection<E> flatten(Collection<? extends Collection<E>> collection){
        Collection<E> result = new ArrayList<>();
        collection.forEach(result::addAll);
        return Collections.unmodifiableCollection(result);
    }
}
