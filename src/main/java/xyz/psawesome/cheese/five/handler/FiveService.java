package xyz.psawesome.cheese.five.handler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import xyz.psawesome.cheese.dto.PowerBallResult;
import xyz.psawesome.cheese.five.dto.FiveStepDto;
import xyz.psawesome.cheese.five.entity.FiveResultDocument;
import xyz.psawesome.cheese.five.entity.FiveStepDocument;
import xyz.psawesome.cheese.five.entity.FiveType;
import xyz.psawesome.cheese.five.service.CheeseFiveResultOperations;
import xyz.psawesome.cheese.five.service.CheeseFiveStepOperations;

import static org.apache.commons.lang3.StringUtils.isBlank;


@Service
@Slf4j
public class FiveService {
  private final CheeseFiveResultOperations resultOperations;
  private final CheeseFiveStepOperations stepOperations;
  @Getter
  private final Sinks.Many<FiveResultDocument> powerBusProcessor;
  @Getter
  private final Sinks.Many<FiveResultDocument> basicBusProcessor;
  @Getter
  private final Sinks.Many<PowerBallResult> resultBusProcessor;

  public FiveService(CheeseFiveResultOperations resultOperations, CheeseFiveStepOperations stepOperations) {
    this.resultOperations = resultOperations;
    this.stepOperations = stepOperations;
//        this.finalBusProcessor = Sinks.many().multicast().onBackpressureBuffer();
    this.powerBusProcessor = Sinks.many().replay().latest();
    this.basicBusProcessor = Sinks.many().replay().latest();
    this.resultBusProcessor = Sinks.many().replay().limit(288);
    resultOperations.searchLastResult(FiveType.POWER).doOnNext(powerBusProcessor::tryEmitNext).subscribe();
  }


  public Mono<FiveStepDto.NextResponse> nextResponse2(String userId, String subnetId, FiveType fiveType) {
    // 1. 유저의 맨 마지막 배팅 or default
    // 2. 마지막 배팅이 현재 배팅하고 같으면 현재 값 반환
    // 3. 같지 않으면, 마지막 배팅 값 조회 후 정답 여부 판단
    // 4. 정답이면 금액 초기화
    // 4. 오답이면 금액 증가
    return powerBusProcessor.asFlux().log("bus start ->>>>> ").next().log(" bus end ->>> ").doOnNext(res -> log.info("last result algorithm: {}, res: {}", res.getAlgorithm(), res)).zipWith(stepOperations.searchLastMono(userId, subnetId, fiveType).onErrorReturn(FiveStepDocument.forInitSave(userId, subnetId, "", fiveType))).flatMap(lastZip -> {
      var result = lastZip.getT1();
      var step = lastZip.getT2();
      if (isBlank(step.getAlgorithm())) step.setAlgorithm(result.getAlgorithm());

      return step.isAlreadyBat(result) ? stepOperations.searchByIdMono(step.getFiveStepId()).onErrorResume(throwable -> stepOperations.addDocument(step)) : resultOperations.searchMatchPhraseAlgorithmMono(step.getAlgorithm(), step.getFiveType()).doOnNext(s -> log.info("search match result algorithm: {}, res: {}", s.getAlgorithm(), s)).map(matchResult -> FiveStepDocument.next(result, matchResult, step)).flatMap(nextStep -> stepOperations.searchLastStepMonoByAlgorithm(userId, subnetId, nextStep.getAlgorithm(), fiveType).onErrorReturn(nextStep).publishOn(Schedulers.boundedElastic()).doOnNext(s -> {
        log.info("is new : {}, toString:\n{}", s.isNew(), s);
        if (s.isNew()) stepOperations.addDocument(s).subscribe();
      }));
    }).doOnNext(s -> log.info("final next step : {}", s)).map(FiveStepDto.NextResponse::new);
  }


  public Mono<FiveResultDocument> currentResultDocument() {
    return powerBusProcessor.asFlux().next();
  }


  @Bean
  CommandLineRunner syncFiveResult() {
    return args -> {
      powerBusProcessor.asFlux().log("sync process run ->>>>>>>>>>>>>>>>").flatMap(resultOperations::addDocument).log("sync complete to  elastic").subscribe();
    };
  }
}
