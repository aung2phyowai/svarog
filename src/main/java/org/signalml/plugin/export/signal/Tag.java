/* Tag.java created 2007-09-28
 *
 */

package org.signalml.plugin.export.signal;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.model.ChannelPropertyEditor;
import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.model.PropertyProvider;
import org.springframework.context.MessageSourceResolvable;

/**
 * This class represents a tagged {@link SignalSelection selection} of a signal.
 * Contains the {@link TagStyle style} and annotation of this selection.
 * Allows to compare tagged selections using its left end, length and the
 * number of channel (in this order).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class Tag extends SignalSelection implements Comparable<ExportedTag>, Cloneable, MessageSourceResolvable, PropertyProvider, ExportedTag {

    //TODO (can't extend SignalSelection due to strange xstream behaviour)
	private static final long serialVersionUID = 1L;

    /**
     * {@link TagStyle style} of this tagged selection
     */
	private TagStyle style;
	
	/**
	 * A string -> string map of Tag attributes (like 'annotation').
	 */
	private HashMap<String, String> attributes;

	/**
     * Constructor. Creates a tagged selection from a given
     * {@link SignalSelection selection} with a given {@link TagStyle style}
     * and annotation.
     * @param style the style of the tagged selection
     * @param signalSelection a signal selection to be copied/used
     * @param annotation an annotation of a tagged selection
     */
	public Tag(TagStyle style, SignalSelection signalSelection, String annotation) {
		super(style.getType(), signalSelection.getPosition(), signalSelection.getLength(), signalSelection.getChannel());
		this.style = style;
		this.attributes = new HashMap<String, String>();
		this.setAnnotation(annotation);
	}

    /**
     * Constructor. Creates a tagged selection with a given
     * {@link TagStyle style}, starting position, length, annotation and
     * the number of the channel.
     * @param style the style of the tagged selection
     * @param d the position from which the selection starts
     * @param e the length of the selection
     * @param channel a number of a channel which this selection should
     * concern. CHANNEL_NULL if selection should concern all channels
     * @param annotation an annotation of a tagged selection
     */
	public Tag(TagStyle style, double d, double e, int channel, String annotation) {
		super(style.getType(), d, e, channel);
		this.style = style;
		this.attributes = new HashMap<String, String>();
		this.setAnnotation(annotation);
	}

    /**
     * Constructor. Creates a tagged selection with a given
     * {@link TagStyle style}, starting position and length, but without
     * any annotation. Selection will concern all channels.
     * @param style the style of the tagged selection.
     * @param position the position from which the selection starts
     * @param length the length of the selection
     */
	public Tag(TagStyle style, double position, double length) {
		super((style != null ? style.getType() : SignalSelectionType.CHANNEL), position, length);
		this.style = style;
		this.attributes = new HashMap<String, String>();

	}

    /**
     * Constructor. Creates a tagged selection with a given
     * {@link TagStyle style}, starting position, length and the number of
     * channel, but without any annotation.
     * @param style the style of the tagged selection
     * @param position the position from which the selection starts
     * @param length the length of the selection
     * @param channel a number of a channel which this selection should
     */
	public Tag(TagStyle style, double position, double length, int channel) {
		super(style.getType(), position, length, channel);
		this.style = style;
		this.attributes = new HashMap<String, String>();

	}

    /**
     * Copy constructor.
     * @param tag the tagged selection to be copied
     */
	public Tag(Tag tag) {
		this(tag.style, tag.position, tag.length, tag.channel, tag.getAnnotation());
	}
	
	/**
	 * Copy constructor.
	 * Creates a tag using the parameters provided by given interface.
	 * @param tag the object which parameters are to be copied
	 */
	public Tag(ExportedTag tag){
		this(new TagStyle(tag.getStyle()), tag.getPosition(), tag.getLength(), tag.getChannel(), tag.getAnnotation());
		this.attributes = new HashMap<String, String>();
	}

    /**
     * Returns the style of this tagged selection.
     * @return the style of this tagged selection
     */
	@Override
	public TagStyle getStyle() {
		return style;
	}

    /**
     * Returns the annotation of this tagged selection.
     * @return the annotation of this tagged selection
     */
	@Override
	public String getAnnotation() {
		return this.attributes.get("annotation");
	}

    /**
     * Sets the annotation of this tagged selection.
     * @param annotation the annotation to be set
     */
	@Override
	public void setAnnotation(String annotation) {
		this.setAttribute("annotation", annotation);
	}

	/**
	 * Returns a string-string HashMap of tag attributes.
	 * @return a string-string HashMap of tag attributes.
	 */
    public HashMap<String, String> getAttributes() {
		return attributes;
	}
	
    /**
     * Returns a string representation of 'key' tag's attribute or null 
     * if there is no attribute with key 'key'.
     * @param key a string-key for the attribute to be returned
     * @return a string representation of 'key' attribute or null
     * if there is no attribute with key 'key'
     */
	public String getAttribute(String key) {
		return this.attributes.get(key);
	}

	/**
	 * Set tag's attribute with string key 'key' and string value 'value'.
	 * @param key a key for the attribute to be set
	 * @param value a value for the attribute to be set
	 */
	public void setAttribute(String key, String value) {
		this.attributes.put(key, value);
	}
    /**
     * Returns whether this tagged selection is a marker.
     * @return true if this tagged selection is a marker, false otherwise
     */
	@Override
	public boolean isMarker() {
		return (style != null ? style.isMarker() : false);
	}

    /**
     * Compares the current object to given.
     * The comparison uses the following characteristics in turn:
     * starting position, length, the channel number,
     * and the TagStyle of this tag. The first characteristic that
     * doesn't match determines the outcome of the comparison.
     * @param t a tagged selection to be compared with the current object
     * @return &gt; 0 if the current object is greater than given;
     * &lt; 0 if current is smaller than given;
     * 0 if the selections are equal.
     */
	public int compareTo(Tag t) {

		double test = position - t.position;
		if (test == 0) {
			test = length - t.length;
			if (test == 0) {
				test = channel - t.channel;
				if (((int) test) == 0) {
					if (style != null && t.style != null) {
						return style.compareTo(t.style);
					} else {
						return 0;
					}
				}
			}
		}
		return (int) Math.signum(test);

	}

    /**
     * Checks if this tagged selection is equal to given.
     * Uses {@link #compareTo(Tag)}.
     * @param obj an object to be compared with this tagged selection
     * @return true if a given object is equal to this tagged selection,
     * false otherwise
     */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Tag)) {
			return false;
		}
		return (this.compareTo((Tag) obj) == 0);
	}

    /**
     * Creates a copy of this tagged selection.
     * @return a copy of this tagged selection
     */
	@Override
	public Tag clone() {
		return new Tag(style,position,length,channel,this.getAnnotation());
	}

	@Override
	public String toString() {
		return style.getName() + ": " + position + " -> " + (position+length);
	}

	@Override
	public Object[] getArguments() {
		return new Object[] {
		               style.getDescriptionOrName(),
		               position,
		               length,
		               position+length,
		               channel
		       };
	}

	@Override
	public String[] getCodes() {
		return new String[] {
		               (channel == CHANNEL_NULL ? "tagWithoutChannel" : "tagWithChannel")
		       };
	}

	@Override
	public String getDefaultMessage() {
		return toString();
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor("property.tag.style", "style", Tag.class, "getStyle", null));
		list.add(new LabelledPropertyDescriptor("property.tag.position", "position", Tag.class));
		list.add(new LabelledPropertyDescriptor("property.tag.length", "length", Tag.class));
		if (channel != CHANNEL_NULL) {
			LabelledPropertyDescriptor channel = new LabelledPropertyDescriptor("property.tag.channel", "channel", Tag.class);
			channel.setPropertyEditorClass(ChannelPropertyEditor.class);
			list.add(channel);
		}
		list.add(new LabelledPropertyDescriptor("property.tag.annotation", "annotation", Tag.class));

		return list;

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.ExportedTag#compareTo(org.signalml.plugin.export.signal.ExportedTag)
	 */
	@Override
	public int compareTo(ExportedTag t) {
		double test = position - t.getPosition();
		if (test == 0) {
			test = length - t.getLength();
			if (test == 0) {
				test = channel - t.getChannel();
				if (((int) test) == 0) {
					if (style != null && t.getStyle() != null) {
						TagStyle newStyle = new TagStyle(t.getStyle());
						return style.compareTo(newStyle);
					} else {
						return 0;
					}
				}
			}
		}
		return (int) Math.signum(test);
	}

}
