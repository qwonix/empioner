package ru.qwonix.empioner.telegram.service.api.spring.graphql.reactive;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import graphql.ErrorType;
import graphql.GraphqlErrorException;
import ru.qwonix.empioner.telegram.id.EpisodeId;
import ru.qwonix.empioner.telegram.exception.EpisodeNotFoundException;
import ru.qwonix.empioner.telegram.exception.VideoNotFoundException;
import ru.qwonix.empioner.telegram.service.api.graphql.model.CreateEpisodeVideoInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.UpdateVideoInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.EpisodeVideoDetailsInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.EpisodeVideoDetails;
import ru.qwonix.empioner.telegram.service.model.EpisodeVideoRequest;
import ru.qwonix.empioner.telegram.service.model.UpdateVideoRequest;
import ru.qwonix.empioner.telegram.service.model.VideoMetadataRequest;
import ru.qwonix.empioner.telegram.id.TelegramFileId;
import ru.qwonix.empioner.telegram.id.TelegramFileUniqueId;
import ru.qwonix.empioner.telegram.id.VideoGroupId;
import ru.qwonix.empioner.telegram.id.VideoId;
import ru.qwonix.empioner.telegram.service.api.VideoApi;
import ru.qwonix.empioner.telegram.service.api.graphql.model.AddVideoInput;
import ru.qwonix.empioner.telegram.service.api.graphql.model.VideoInput;
import ru.qwonix.empioner.telegram.service.api.mapper.VideoMapper;

@DgsComponent
@RequiredArgsConstructor
public class VideoGraphQLController {

    private final VideoApi videoApi;
    private final VideoMapper mapper;

    @DgsQuery
    public Mono<VideoInput> maxPriorityVideo(@InputArgument VideoGroupId id) {
        return Mono.fromCallable(() -> videoApi.findMaxPriorityInGroup(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsQuery
    public Mono<VideoInput> videoById(@InputArgument VideoId id) {
        return Mono.fromCallable(() -> videoApi.findById(id))
                .map(optional -> optional.map(mapper::toInput))
                .flatMap(optional -> optional.map(Mono::just).orElse(Mono.empty()));
    }

    @DgsQuery
    public Flux<VideoInput> videosByVideoGroupId(@InputArgument VideoGroupId id) {
        return Flux.defer(() -> Flux.fromIterable(videoApi.findAllByVideoGroupId(id)))
                .map(mapper::toInput);
    }

    @DgsMutation
    public Mono<VideoInput> addVideoToEpisode(@InputArgument("episodeId") EpisodeId episodeId,
                                               @InputArgument("input") CreateEpisodeVideoInput input) {
        return Mono.fromCallable(() -> videoApi.addToEpisode(episodeId, new EpisodeVideoRequest(
                        input.getDescription(), input.getTelegramFileId(), input.getTelegramFileUniqueId(),
                        input.getPriority(), input.getIsAvailable(), metadata(input.getDetails()))))
                .subscribeOn(Schedulers.boundedElastic()).map(mapper::toInput)
                .onErrorMap(IllegalArgumentException.class, VideoGraphQLController::inputError)
                .onErrorMap(EpisodeNotFoundException.class, VideoGraphQLController::inputError);
    }

    @DgsMutation
    public Mono<VideoInput> updateVideo(@InputArgument("id") VideoId id, @InputArgument("input") UpdateVideoInput input) {
        return Mono.fromCallable(() -> videoApi.update(id, new UpdateVideoRequest(
                        input.getDescription(), input.getTelegramFileId(), input.getTelegramFileUniqueId(),
                        input.getPriority(), input.getIsAvailable(), metadata(input.getDetails()))))
                .subscribeOn(Schedulers.boundedElastic()).map(mapper::toInput)
                .onErrorMap(IllegalArgumentException.class, VideoGraphQLController::inputError)
                .onErrorMap(VideoNotFoundException.class, VideoGraphQLController::inputError);
    }

    @DgsQuery
    public Mono<EpisodeVideoDetails> videoDetailsById(@InputArgument("id") VideoId id) {
        return Mono.fromCallable(() -> videoApi.findDetails(id)).subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.map(d -> Mono.just(EpisodeVideoDetails.newBuilder()
                        .width(d.width()).height(d.height()).duration(d.duration()).mimeType(d.mimeType())
                        .fileSize(d.fileSize()).fileName(d.fileName()).build())).orElse(Mono.empty()));
    }

    private static VideoMetadataRequest metadata(EpisodeVideoDetailsInput d) {
        return d == null ? null : new VideoMetadataRequest(d.getWidth(), d.getHeight(), d.getDuration(),
                d.getMimeType(), d.getFileSize(), d.getFileName());
    }

    private static GraphqlErrorException inputError(RuntimeException error) {
        return GraphqlErrorException.newErrorException().message(error.getMessage())
                .errorClassification(ErrorType.DataFetchingException).cause(error).build();
    }

    @DgsMutation
    public Mono<VideoId> create(@InputArgument AddVideoInput video, @InputArgument Boolean needCreateNewVideoGroup) {
        return Mono.fromCallable(() -> videoApi.create(mapper.toRequest(video), needCreateNewVideoGroup));
    }

    @DgsMutation
    public Mono<Boolean> updateTelegramFileId(
            @InputArgument TelegramFileUniqueId id,
            @InputArgument TelegramFileId newTelegramFileId) {
        return Mono.fromCallable(() -> videoApi.updateTelegramFileIdByTelegramFileUniqueId(
                id,
                newTelegramFileId
        ));
    }
}
