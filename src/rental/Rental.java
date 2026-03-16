package rental;

import java.time.LocalDate;

public class Rental {
    private final int id;
    private final int comicId;
    private final int memberId;
    private final LocalDate rentDate;
    private LocalDate returnDate;

    public Rental(int id, int comicId, int memberId, LocalDate rentDate) {
        this.id = id;
        this.comicId = comicId;
        this.memberId = memberId;
        this.rentDate = rentDate;
    }

    public int getId() {
        return id;
    }

    public int getComicId() {
        return comicId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}

