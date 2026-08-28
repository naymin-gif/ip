public class Deadline extends Task {
    protected String finishBy;
    public Deadline(String description, String by) {
        super(description);
        this.finishBy = by;
    }

    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + finishBy;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.finishBy + ")";
    }
}
