package com.kosta.readdam.entity.enums;

public enum ReportCategory {
    write_short("write_short", "writeshort_id"),
    write("write", "write_id"),
    write_comment("write_comment", "write_comment_id"),
    book_review("book_review", "book_review_id"),
    class_qna("class_qna", "class_qna_id"),
    class_review("class_review", "class_review_id"),
    other_place_review("other_place_review", "other_place_review_id"),
    place_review("place_review", "place_review_id");

    private final String tableName;
    private final String idColumn;

    ReportCategory(String tableName, String idColumn) {
        this.tableName = tableName;
        this.idColumn = idColumn;
    }
    
    public String getTableName() {
        return tableName;
    }

    public String getIdColumn() {
        return idColumn;
    }
}

