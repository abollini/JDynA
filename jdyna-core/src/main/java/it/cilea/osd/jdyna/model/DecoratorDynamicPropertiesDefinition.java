/*
 * JDynA, Dynamic Metadata Management for Java Domain Object
 *
 * Copyright (c) 2008, CILEA and third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by CILEA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License v3 or any later version, as published 
 * by the Free Software Foundation, Inc. <http://fsf.org/>.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package it.cilea.osd.jdyna.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@NamedQueries( {
    @NamedQuery(name = "DecoratorDynamicPropertiesDefinition.findAll", query = "from DecoratorDynamicPropertiesDefinition order by id"),
    @NamedQuery(name = "DecoratorDynamicPropertiesDefinition.uniqueContainableByDecorable", query = "from DecoratorDynamicPropertiesDefinition where real.id = ?"),
    @NamedQuery(name = "DecoratorDynamicPropertiesDefinition.uniqueContainableByShortName", query = "from DecoratorDynamicPropertiesDefinition where real.shortName = ?")
    
})
@DiscriminatorValue(value="propertiesdefinitiondynamicobject")
public class DecoratorDynamicPropertiesDefinition extends
        ADecoratorPropertiesDefinition<DynamicPropertiesDefinition>
{

    
    @OneToOne(optional=true)
    @JoinColumn(name="propertiesdefinitiondynamicobject_fk")
    @Cascade(value = {CascadeType.ALL,CascadeType.DELETE_ORPHAN})
    private DynamicPropertiesDefinition real;
    
    
    @Override
    public Class getAnagraficaHolderClass()
    {
       return DynamicObject.class;
    }

    @Override
    public Class getPropertyHolderClass()
    {
        return NestedProperty.class;
    }

    @Override
    public Class getDecoratorClass()
    {
        return DecoratorDynamicPropertiesDefinition.class;
    }

    @Transient
    public AWidget getRendering() {
        return this.real.getRendering();
    }

    @Transient
    public String getShortName() {
        return this.real.getShortName();
    }

    @Transient
    public boolean isMandatory() {
        return this.real.isMandatory();
    }

    @Transient
    public String getLabel() {
        return this.real.getLabel();
    }

    @Transient
    public int getPriority() {
        return this.real.getPriority();
    }

    @Transient
    public Integer getAccessLevel() {
        return this.real.getAccessLevel();
    }

    @Override
    public boolean getRepeatable() {
        return this.real.isRepeatable();
    }

    @Override
    public int compareTo(IContainable o) {
        DynamicPropertiesDefinition oo = null;
        if(o instanceof DecoratorDynamicPropertiesDefinition) {
            oo = (DynamicPropertiesDefinition)o.getObject();
            return this.real.compareTo(oo);
        }
        return 0;
    }

    @Override
    public void setReal(DynamicPropertiesDefinition object)
    {
       this.real = object;        
    }

    @Override
    public DynamicPropertiesDefinition getObject()
    {
        return this.real;
    }   

}
