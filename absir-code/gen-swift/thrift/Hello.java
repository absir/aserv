package thrift;

import com.facebook.swift.codec.ThriftField.Requiredness;

import static com.google.common.base.MoreObjects.toStringHelper;

@ThriftStruct("Hello")
public final class Hello {
    private final int id;
    private final long id2;

    @ThriftConstructor
    public Hello(
            @ThriftField(value = 1, name = "id", requiredness = Requiredness.NONE) final int id,
            @ThriftField(value = 2, name = "id2", requiredness = Requiredness.NONE) final long id2
    ) {
        this.id = id;
        this.id2 = id2;
    }

    @ThriftField(value = 1, name = "id", requiredness = Requiredness.NONE)
    public int getId() {
        return id;
    }

    @ThriftField(value = 2, name = "id2", requiredness = Requiredness.NONE)
    public long getId2() {
        return id2;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("id2", id2)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Hello other = (Hello) o;

        return
                Objects.equals(id, other.id) &&
                        Objects.equals(id2, other.id2);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{
                id,
                id2
        });
    }

    public static class Builder {
        private int id;
        private long id2;

        public Builder() {
        }

        public Builder(Hello other) {
            this.id = other.id;
            this.id2 = other.id2;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setId2(long id2) {
            this.id2 = id2;
            return this;
        }

        public Hello build() {
            return new Hello(
                    this.id,
                    this.id2
            );
        }
    }
}
