package net.codersky.skyutils.java.math.chance;

import net.codersky.skyutils.java.math.SkyNumbers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * A very basic {@link ChanceGenerator} for generic element types.
 * 
 * @author xDec0de_
 *
 * @param <T> The type of elements to generate
 * 
 * @since SkyUtils 1.0.0
 */
public class GenericChanceGenerator<T> implements ChanceGenerator<T> {

	private final HashMap<T, Float> map = new HashMap<>();

	/**
	 * Adds a new <b>element</b> to be {@link #generate()
	 * generated} randomly with an specific <b>chance</b>
	 * 
	 * @param element the element to add.
	 * @param chance the chance this element will have to
	 * be present on the {@link List} returned by {@link #generate()}
	 * each time the method is called. This chance is a percentage
	 * that must be higher than 0 and smaller than 100.
	 * 
	 * @return This {@link ChanceGenerator}.
	 * 
	 * @since SkyUtils 1.0.0
	 */
	@Override
	public GenericChanceGenerator<T> add(T element, float chance) {
		if (chance > 0)
			map.put(element, chance);
		return this;
	}

	@NotNull
	@Override
	public List<T> generate() {
		final List<T> result = new ArrayList<>(map.size());
		for (Entry<T, Float> entry : map.entrySet())
			if (SkyNumbers.tryChance(entry.getValue()))
				result.add(entry.getKey());
		return result;
	}

	/**
	 * Works in a similar way to {@link #generate()} but limits the
	 * generated elements to a <b>max</b> amount. Keep in mind that the
	 * rarest elements take priority, so for example if we have two elements,
	 * "Common" (100% chance) and "Rare" (25% chance) and the <b>max</b> amount
	 * to generate is 1, if the 25% chance of getting the "Rare" element was
	 * successful, the list will only contain "Rare" as it has a higher priority
	 * to be generated than "Common".
	 * 
	 * @param max the maximum size of the generated {@link List}.
	 * 
	 * @return A {@link List} with {@link #add(Object, float) added}
	 * elements by chance limited to a certain <b>max</b> size.
	 * May be empty if no element was generated by chance.
	 * 
	 * @since SkyUtils 1.0.0
	 */
	@NotNull
	@Override
	public List<T> generate(int max) {
		final HashMap<T, Float> results = new HashMap<>(map.size());
		for (Entry<T, Float> entry : map.entrySet())
			if (SkyNumbers.tryChance(entry.getValue()))
				results.put(entry.getKey(), entry.getValue());
		return limitElements(results, max);
	}
}