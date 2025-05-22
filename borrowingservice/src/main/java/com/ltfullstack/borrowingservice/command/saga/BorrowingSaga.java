package com.ltfullstack.borrowingservice.command.saga;

import com.ltfullstack.borrowingservice.command.command.DeleteBorrowingCommand;
import com.ltfullstack.borrowingservice.command.event.BorrowingCreatedEvent;
import com.ltfullstack.borrowingservice.command.event.BorrowingDeletedEvent;
import com.ltfullstack.commanservice.command.RollBackStatusBookCommand;
import com.ltfullstack.commanservice.command.UpdateStatusBookCommnand;
import com.ltfullstack.commanservice.event.BookRollBackStatusEvent;
import com.ltfullstack.commanservice.event.BookUpdateStatusEvent;
import com.ltfullstack.commanservice.model.BookResponseCommonModel;
import com.ltfullstack.commanservice.model.EmployeeResponseCommonModel;
import com.ltfullstack.commanservice.queries.GetBookDetailQuery;
import com.ltfullstack.commanservice.queries.GetDetailEmployeeQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class BorrowingSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    private void handle(BorrowingCreatedEvent event) {
        log.info("BorrowingCreatedEvent in saga for BookId: " + event.getBookId() + " : EmployeeId: " + event.getEmployeeId());
        try {
            GetBookDetailQuery getBookDetailQuery = new GetBookDetailQuery(event.getBookId());
            BookResponseCommonModel bookRespronseCommonModel = queryGateway.query(getBookDetailQuery,
                    ResponseTypes.instanceOf(BookResponseCommonModel.class)).join();
            if (!bookRespronseCommonModel.getIsReady()) {
                throw new Exception("The book has been borrowed.");
            } else {
                SagaLifecycle.associateWith("bookId", event.getBookId());
                UpdateStatusBookCommnand command = new UpdateStatusBookCommnand(event.getBookId(), false, event.getEmployeeId(), event.getId());
                commandGateway.sendAndWait(command);
            }
        } catch (Exception ex) {
            rollbackBorrowingRecord(event.getId());
            log.error(ex.getMessage());
        }
    }

    @SagaEventHandler(associationProperty = "bookId")
    private void handle(BookUpdateStatusEvent event) {
        log.info("BookUpdateStatusEvent in Saga for BookId: " + event.getBookId());
        try {
            GetDetailEmployeeQuery query =  new GetDetailEmployeeQuery(event.getEmployeeId());
            EmployeeResponseCommonModel employeeModel = queryGateway.query(query, ResponseTypes.instanceOf(EmployeeResponseCommonModel.class)).join();
            if(employeeModel.getIsDisciplined()){
                throw new Exception("The employee has been disciplined");
            }else{
                log.info("Successfully borrowed books");
                SagaLifecycle.end();
            }
        } catch (Exception ex) {
            rollbackBookStatus(event.getBookId(), event.getEmployeeId(), event.getBorrowingId());
            log.error(ex.getMessage());
        }

    }

    private void rollbackBorrowingRecord(String id) {
        DeleteBorrowingCommand command = new DeleteBorrowingCommand(id);
        commandGateway.sendAndWait(command);
    }

    private void rollbackBookStatus(String bookId, String employeeId, String borrowingId) {
        SagaLifecycle.associateWith("bookId", bookId);
        RollBackStatusBookCommand command = new RollBackStatusBookCommand(bookId, true, employeeId, borrowingId);
        commandGateway.sendAndWait(command);
        rollbackBorrowingRecord(bookId);
    }

    @SagaEventHandler(associationProperty = "bookId")
    private void handle(BookRollBackStatusEvent event){
        log.info("BookRollBackStatusEvent is saga for book Id: {}" + event.getBookId());
        rollbackBorrowingRecord(event.getBorrowingId());
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    private void handle(BorrowingDeletedEvent event){
        log.info("BorrowDeletedEvent in Saga for Borrowing Id: {}" + event.getId());
        SagaLifecycle.end();
    }
}
