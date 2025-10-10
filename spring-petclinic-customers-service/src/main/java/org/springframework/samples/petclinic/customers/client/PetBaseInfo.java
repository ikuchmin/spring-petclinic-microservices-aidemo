package org.springframework.samples.petclinic.customers.client;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.Objects;

/**
 * DTO for {@link org.springframework.samples.petclinic.customers.model.Pet}
 */
public class PetBaseInfo {
    private final String name;
    private final Date birthDate;
    @NotBlank
    private final String ownerFirstName;
    @NotBlank
    private final String ownerLastName;
    @NotBlank
    private final String ownerAddress;

    public PetBaseInfo(String name, Date birthDate, String ownerFirstName, String ownerLastName, String ownerAddress) {
        this.name = name;
        this.birthDate = birthDate;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.ownerAddress = ownerAddress;
    }

    public String getName() {
        return name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetBaseInfo entity = (PetBaseInfo) o;
        return Objects.equals(this.name, entity.name) &&
            Objects.equals(this.birthDate, entity.birthDate) &&
            Objects.equals(this.ownerFirstName, entity.ownerFirstName) &&
            Objects.equals(this.ownerLastName, entity.ownerLastName) &&
            Objects.equals(this.ownerAddress, entity.ownerAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, birthDate, ownerFirstName, ownerLastName, ownerAddress);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
            "name = " + name + ", " +
            "birthDate = " + birthDate + ", " +
            "ownerFirstName = " + ownerFirstName + ", " +
            "ownerLastName = " + ownerLastName + ", " +
            "ownerAddress = " + ownerAddress + ")";
    }
}
