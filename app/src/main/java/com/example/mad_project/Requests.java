package com.example.mad_project;

public class Requests {
    private String email, requestDate, issuedDate, returnDate, status, key, bookRef;
    private Book book;

    public Requests() {
    }

    public Requests(String email, String requestDate, String issuedDate, String returnDate, String status, String bookRef) {
        this.email = email;
        this.requestDate = requestDate;
        this.issuedDate = issuedDate;
        this.returnDate = returnDate;
        this.status = status;
        this.bookRef = bookRef;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBookRef() {
        return bookRef;
    }

    public void setBookRef(String bookRef) {
        this.bookRef = bookRef;
    }
}
