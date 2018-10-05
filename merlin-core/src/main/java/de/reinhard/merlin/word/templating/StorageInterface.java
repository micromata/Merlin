package de.reinhard.merlin.word.templating;

import java.util.List;

/**
 * Implement this class for registering, storing, updating and accessing templates, template definitions and
 * definition file for the serial template functionality.
 * <br/>
 * You can use the {@link FileDescriptor} for storing all the files in the file system or you use them, for storing
 * everything e. g. in a data-base. Then the directory of the {@link FileDescriptor} should be interpreted as an area
 * and the relativeDirectory as a sub area of the item locations.
 */
public interface StorageInterface {
    /**
     * @param descriptor Descriptor containing the directory (optional), the relative path inside this directory (optional)
     *                   and the file name (as well optional). If null, all directories (areas) will be searched for the given
     *                   templateDefinitionId.
     * @param templateDefinitionId The id of the template definition as specified inside the template defintion itself. Can't be null.
     * @return Matching TemplateDefinitions if found, otherwise an empty list is returned.
     */
    public List<TemplateDefinition> getTemplateDefinition(FileDescriptor descriptor, String templateDefinitionId);

    /**
     * Stores a given templateDefinition.
     * @param descriptor Where to store the given templateDefinition.
     * @param templateDefinition The templateDefinition to store.
     */
   // public void putTemplateDefinition(FileDescriptor descriptor, TemplateDefinition templateDefinition);

    /**
     *
     * @param canonicalPath Primary key or canonicalPath specifying the template.
     * @param template The template to store.
     */
   // public void putTemplate(String canonicalPath, Template template);
}
